package tgb.btc.rce.service.processors;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.service.process.CalculateService;
import tgb.btc.library.vo.calculate.DealAmount;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineCalculatorButton;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.MessageService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.FunctionPropertiesUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineCalculatorData;
import tgb.btc.rce.vo.InlineCalculatorVO;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

import static tgb.btc.rce.enums.InlineCalculatorButton.*;

@CommandProcessor(command = Command.INLINE_CALCULATOR, step = 1)
public class InlineCalculator extends Processor {

    private IDealPropertyService dealPropertyService;

    private ExchangeService exchangeService;

    private CalculateService calculateService;

    private DealProcessor dealProcessor;

    private KeyboardService keyboardService;

    private IUpdateDispatcher updateDispatcher;

    private MessageService messageService;

    public static ConcurrentHashMap<Long, InlineCalculatorVO> cache = new ConcurrentHashMap<>();

    @Autowired
    public void setDealPropertyService(IDealPropertyService dealPropertyService) {
        this.dealPropertyService = dealPropertyService;
    }

    @Autowired
    public void setDealProcessor(DealProcessor dealProcessor) {
        this.dealProcessor = dealProcessor;
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
            modifyUserService.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
            dealProcessor.run(update);
            return;
        }
        InlineCalculatorVO calculator = cache.get(chatId);
        if (update.hasMessage() && !calculator.getOn()) {
            if (!exchangeService.calculateDealAmount(chatId, UpdateUtil.getBigDecimalFromText(update))) return;
            modifyUserService.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
            updateDispatcher.runProcessor(Command.DEAL, chatId, update);
            return;
        } else if (update.hasMessage()) {
            responseSender.sendMessage(chatId, "Для ручного ввода суммы нажмите \""+ SWITCH_CALCULATOR.getData() + "\".");
            return;
        }
        CallbackQuery callbackQuery = update.getCallbackQuery();
        InlineCalculatorData data = new InlineCalculatorData(callbackQuery.getData());
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
                modifyUserService.updateStepAndCommandByChatId(chatId, Command.DEAL.name(), DealProcessor.AFTER_CALCULATOR_STEP);
                updateDispatcher.runProcessor(Command.DEAL, chatId, update);
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
                    calculator.getFiatCurrency(), dealType, !isSwitched, BooleanUtils.isTrue(FunctionPropertiesUtil.getSumToReceive(calculator.getCryptoCurrency())))
                    : null;
            calculator.setDealAmount(dealAmount);
        }
        responseSender.sendEditedMessageText(chatId, messageId,
                messageService.getInlineCalculatorMessage(dealType, calculator, dealAmount),
                keyboardService.getInlineCalculator(chatId));
    }
}
