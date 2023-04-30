package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.MessageService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class ExchangeServiceNew {

    private IResponseSender responseSender;

    private UserService userService;

    private KeyboardService keyboardService;

    private DealService dealService;

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    public void askForCurrency(Long chatId, DealType dealType) {
        messageService.sendMessageAndSaveMessageId(chatId, MessagePropertiesUtil.getChooseCurrency(dealType), keyboardService.getCurrencies(dealType));
    }

    public void askForSum(Long chatId, CryptoCurrency currency, DealType dealType) {
        String text = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM),
                dealService.getCryptoCurrencyByPid(userService.getCurrentDealByChatId(chatId)));
        ReplyKeyboard keyboard = getCalculatorKeyboard(currency, dealType);
        messageService.sendMessageAndSaveMessageId(chatId, text, keyboard);
    }

    private ReplyKeyboard getCalculatorKeyboard(CryptoCurrency currency, DealType dealType) {
        String operation = DealType.BUY.equals(dealType)
                ? "-buy"
                : "-sell";
        return KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                        .text("Калькулятор")
                        .data(currency.getShortName() + operation + " ")
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON), 1);
    }
}
