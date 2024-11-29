package tgb.btc.rce.service.impl.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.IMessageService;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.handler.impl.state.InlineCalculatorHandler;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.vo.InlineCalculatorVO;

public class InlineCalculatorService implements ICalculatorTypeService {

    private IResponseSender responseSender;

    private IReadUserService readUserService;

    private IDealPropertyService dealPropertyService;

    private IKeyboardService keyboardService;

    private IMessageService messageService;

    private IUpdateService updateService;

    private IRedisUserStateService redisUserStateService;

    @Autowired
    public void setRedisUserStateService(IRedisUserStateService redisUserStateService) {
        this.redisUserStateService = redisUserStateService;
    }

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setDealPropertyService(IDealPropertyService dealPropertyService) {
        this.dealPropertyService = dealPropertyService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setKeyboardService(IKeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setMessageService(IMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        DealType dealType = dealPropertyService.getDealTypeByPid(currentDealPid);
        InlineCalculatorVO inlineCalculatorVO = new InlineCalculatorVO();
        inlineCalculatorVO.setCryptoCurrency(dealPropertyService.getCryptoCurrencyByPid(currentDealPid));
        inlineCalculatorVO.setFiatCurrency(dealPropertyService.getFiatCurrencyByPid(currentDealPid));
        inlineCalculatorVO.setSwitched(false);
        inlineCalculatorVO.setOn(true);
        InlineCalculatorHandler.cache.put(chatId, inlineCalculatorVO);
        responseSender.sendMessage(chatId, messageService.getInlineCalculatorMessage(dealType, inlineCalculatorVO),
                keyboardService.getInlineCalculator(chatId));
        redisUserStateService.save(chatId, UserState.INLINE_CALCULATOR);
    }

}
