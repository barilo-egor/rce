package tgb.btc.lib.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.conditional.InlineCalculatorCondition;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.DealType;
import tgb.btc.lib.repository.DealRepository;
import tgb.btc.lib.repository.UserRepository;
import tgb.btc.lib.service.ICalculatorTypeService;
import tgb.btc.lib.service.IResponseSender;
import tgb.btc.lib.service.processors.InlineCalculator;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineCalculatorVO;

@Service
@Conditional(InlineCalculatorCondition.class)
public class InlineCalculatorService implements ICalculatorTypeService {

    private IResponseSender responseSender;
    private UserService userService;
    private DealRepository dealRepository;
    private KeyboardService keyboardService;
    private UserRepository userRepository;
    private MessageService messageService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        InlineCalculatorVO inlineCalculatorVO = new InlineCalculatorVO();
        inlineCalculatorVO.setCryptoCurrency(dealRepository.getCryptoCurrencyByPid(currentDealPid));
        inlineCalculatorVO.setFiatCurrency(dealRepository.getFiatCurrencyByPid(currentDealPid));
        inlineCalculatorVO.setSwitched(false);
        inlineCalculatorVO.setOn(true);
        InlineCalculator.cache.put(chatId, inlineCalculatorVO);
        responseSender.sendMessage(chatId, messageService.getInlineCalculatorMessage(dealType, inlineCalculatorVO),
                keyboardService.getCalculator(chatId));
        userRepository.updateStepAndCommandByChatId(chatId, Command.INLINE_CALCULATOR, 1);
    }

}
