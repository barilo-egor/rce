package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MessagePropertiesUtil;

@Service
public class ExchangeServiceNew {

    private IResponseSender responseSender;

    private UserService userService;

    private KeyboardService keyboardService;

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
        String text = MessagePropertiesUtil.getChooseCurrency(dealType);
        ReplyKeyboard keyboard = keyboardService.getCurrencies(dealType);
        responseSender.sendMessage(chatId, text, keyboard)
                .ifPresent(message -> userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }
}
