package tgb.btc.rce.service.handler.impl.state.deal;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.process.CalculateService;
import tgb.btc.library.vo.calculate.DealAmount;
import tgb.btc.rce.enums.InlineCalculatorButton;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IFunctionsService;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.IMessageService;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineCalculatorData;
import tgb.btc.rce.vo.InlineCalculatorVO;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

import static tgb.btc.rce.enums.InlineCalculatorButton.*;

@Service
public class InlineCalculatorHandler implements IStateHandler {

    public static ConcurrentHashMap<Long, InlineCalculatorVO> cache = new ConcurrentHashMap<>();

    private final IDealPropertyService dealPropertyService;

    private final ExchangeService exchangeService;

    private final CalculateService calculateService;

    private final DealHandler dealHandler;

    private final IKeyboardService keyboardService;

    private final IMessageService messageService;

    private final IFunctionsService functionsService;

    private final IMessagePropertiesService messagePropertiesService;

    private final ICallbackDataService callbackDataService;

    private final IUpdateService updateService;

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final IRedisUserStateService redisUserStateService;

    private final IModifyUserService modifyUserService;

    private final IRedisStringService redisStringService;

    public InlineCalculatorHandler(IDealPropertyService dealPropertyService, ExchangeService exchangeService,
                                   CalculateService calculateService, DealHandler dealHandler, IKeyboardService keyboardService,
                                   IMessageService messageService, IFunctionsService functionsService,
                                   IMessagePropertiesService messagePropertiesService,
                                   ICallbackDataService callbackDataService, IUpdateService updateService,
                                   IReadUserService readUserService,
                                   IResponseSender responseSender, IRedisUserStateService redisUserStateService,
                                   IModifyUserService modifyUserService, IRedisStringService redisStringService) {
        this.dealPropertyService = dealPropertyService;
        this.exchangeService = exchangeService;
        this.calculateService = calculateService;
        this.dealHandler = dealHandler;
        this.keyboardService = keyboardService;
        this.messageService = messageService;
        this.functionsService = functionsService;
        this.messagePropertiesService = messagePropertiesService;
        this.callbackDataService = callbackDataService;
        this.updateService = updateService;
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.redisUserStateService = redisUserStateService;
        this.modifyUserService = modifyUserService;
        this.redisStringService = redisStringService;
    }

    @Override
    public void handle(Update update) {
        Long chatId = updateService.getChatId(update);
        if (update.hasCallbackQuery() && callbackDataService.isCallbackQueryData(CallbackQueryData.BACK, update.getCallbackQuery().getData())) {
            redisUserStateService.save(chatId, UserState.DEAL);
            redisStringService.saveStep(chatId, DealHandler.AFTER_CALCULATOR_STEP);
            dealHandler.handle(update);
            return;
        }
        InlineCalculatorVO calculator = cache.get(chatId);
        if (update.hasMessage() && !calculator.getOn()) {
            if (!exchangeService.calculateDealAmount(chatId, updateService.getBigDecimalFromText(update))) return;
            redisUserStateService.save(chatId, UserState.DEAL);
            redisStringService.saveStep(chatId, DealHandler.AFTER_CALCULATOR_STEP);
            dealHandler.handle(update);
            return;
        } else if (update.hasMessage()) {
            responseSender.sendMessage(chatId, "Для ручного ввода суммы нажмите \""+ SWITCH_CALCULATOR.getData() + "\".");
            return;
        }
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String callbackData = callbackQuery.getData();
        InlineCalculatorData data = new InlineCalculatorData();
        data.setButtonData(callbackDataService.getArgument(callbackData, 1));
        data.setNumber(callbackDataService.getArgument(callbackData, 2));
        String sum = calculator.getSum();
        Boolean isSwitched = calculator.getSwitched();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        InlineCalculatorButton button = InlineCalculatorButton.getByData(data.getButtonData());
        switch (button) {
            case NUMBER:
                if (StringUtils.isNotBlank(sum)) {
                    if (!sum.equals("0")) sum = sum.concat(data.getNumber());
                    else if (data.getNumber().equals("0")) return;
                    else sum = data.getNumber();
                }
                else sum = data.getNumber();
                calculator.setSum(sum);
                break;
            case COMMA:
                if (StringUtils.isBlank(sum) || sum.contains(COMMA.getData())) return;
                else sum = sum.concat(data.getButtonData());
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
                String textMessage = messagePropertiesService.getMessage(PropertiesMessage.DEAL_INPUT_SUM,
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
                redisUserStateService.save(chatId, UserState.DEAL);
                redisStringService.saveStep(chatId, DealHandler.AFTER_CALCULATOR_STEP);
                dealHandler.handle(update);
                return;
        }
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        DealType dealType = dealPropertyService.getDealTypeByPid(currentDealPid);
        DealAmount dealAmount;
        if (COMMA.equals(button) || (NUMBER.equals(button) && (sum.contains(COMMA.getData()) && sum.endsWith("0")))) {
            dealAmount = calculator.getDealAmount();
        } else {
            dealAmount = StringUtils.isNotEmpty(sum)
                    ? calculateService.calculate(chatId, new BigDecimal(sum), calculator.getCryptoCurrency(),
                    calculator.getFiatCurrency(), dealType, !isSwitched,
                    BooleanUtils.isTrue(functionsService.getSumToReceive(calculator.getCryptoCurrency(), calculator.getFiatCurrency())))
                    : null;
            calculator.setDealAmount(dealAmount);
        }
        responseSender.sendEditedMessageText(chatId, messageId,
                messageService.getInlineCalculatorMessage(dealType, calculator, dealAmount),
                keyboardService.getInlineCalculator(chatId));
    }

    @Override
    public UserState getUserState() {
        return UserState.INLINE_CALCULATOR;
    }
}
