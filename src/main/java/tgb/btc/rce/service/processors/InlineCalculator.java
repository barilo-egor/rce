package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.CalculateService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.MessageService;
import tgb.btc.rce.service.impl.UserDiscountService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineCalculatorVO;
import tgb.btc.rce.vo.calculate.DealAmount;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static tgb.btc.rce.enums.InlineCalculatorButton.COMMA;
import static tgb.btc.rce.enums.InlineCalculatorButton.getByData;

@CommandProcessor(command = Command.INLINE_CALCULATOR, step = 1)
public class InlineCalculator extends Processor {

    private DealRepository dealRepository;

    private ExchangeService exchangeService;

    private UserDiscountService userDiscountService;

    private CalculateService calculateService;

    private DealProcessor dealProcessor;

    private KeyboardService keyboardService;

    private IUpdateDispatcher updateDispatcher;

    private MessageService messageService;

    public static ConcurrentHashMap<Long, InlineCalculatorVO> cache = new ConcurrentHashMap<>();

    @Autowired
    public void setDealProcessor(DealProcessor dealProcessor) {
        this.dealProcessor = dealProcessor;
    }

    @Autowired
    public void setUserDiscountService(UserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setExchangeService(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setUpdateDispatcher(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (CallbackQueryUtil.isBack(update)) {
            userRepository.updateStepAndCommandByChatId(chatId, Command.DEAL, DealProcessor.AFTER_CALCULATOR_STEP);
            dealProcessor.run(update);
            return;
        }
        InlineCalculatorVO calculator = cache.get(chatId);
        if (update.hasMessage() && !calculator.getOn()) {
            if (!exchangeService.calculateDealAmount(chatId, UpdateUtil.getBigDecimalFromText(update))) return;
            userRepository.updateStepAndCommandByChatId(chatId, Command.DEAL, DealProcessor.AFTER_CALCULATOR_STEP);
            updateDispatcher.runProcessor(Command.DEAL, chatId, update);
            return;
        } else if (update.hasMessage()) return;
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String[] data = callbackQuery.getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        String sum = calculator.getSum();
        Boolean isSwitched = calculator.getSwitched();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        switch (getByData(data[1])) {
            case NUMBER:
                if (StringUtils.isNotBlank(sum)) {
                    if (!sum.equals("0")) sum = sum.concat(data[2]);
                    else if (data[2].equals("0")) return;
                    else sum = data[2];
                }
                else sum = data[2];
                calculator.setSum(sum);
                break;
            case COMMA:
                if (StringUtils.isBlank(sum) || sum.contains(COMMA.getData())) return;
                else sum = sum.concat(data[1]);
                calculator.setSum(sum);
                break;
            case DEL:
                if (StringUtils.isNotBlank(sum)) sum = StringUtils.chop(sum);
                else return;
                calculator.setSum(sum);
                break;
            case CURRENCY_SWITCHER:
                calculator.setSwitched(!isSwitched);
                sum = StringUtils.EMPTY;
                calculator.setSum(sum);
                break;
            case SWITCH_CALCULATOR:
                String textMessage = MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM,
                        calculator.getCryptoCurrency());
                responseSender.sendEditedMessageText(chatId, messageId, textMessage,
                        keyboardService.getInlineCalculatorSwitcher());
                calculator.setSum(StringUtils.EMPTY);
                calculator.setOn(false);
                return;
            case SWITCH_TO_MAIN_CALCULATOR:
                calculator.setSwitched(false);
                calculator.setOn(true);
                break;
            case READY:
                if (!exchangeService.calculateDealAmount(chatId, new BigDecimal(sum), !isSwitched)) return;
                userRepository.updateStepAndCommandByChatId(chatId, Command.DEAL, DealProcessor.AFTER_CALCULATOR_STEP);
                updateDispatcher.runProcessor(Command.DEAL, chatId, update);
                return;
        }
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        DealAmount dealAmount = StringUtils.isNotEmpty(sum)
                ? calculateService.calculate(new BigDecimal(sum), calculator.getCryptoCurrency(), calculator.getFiatCurrency(), dealType, !isSwitched)
                : null;
        if (Objects.nonNull(dealAmount)) {
            userDiscountService.applyPersonal(chatId, dealType, dealAmount);
            userDiscountService.applyBulk(calculator.getFiatCurrency(), dealType, dealAmount);
        }
        responseSender.sendEditedMessageText(chatId, messageId,
                messageService.getInlineCalculatorMessage(dealType, calculator, dealAmount),
                keyboardService.getCalculator(chatId));
    }
}