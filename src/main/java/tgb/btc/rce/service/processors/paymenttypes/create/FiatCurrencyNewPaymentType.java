package tgb.btc.rce.service.processors.paymenttypes.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE, step = 2)
public class FiatCurrencyNewPaymentType extends Processor {

    private IUserDataService userDataService;

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Override
    public void run(Update update) {
        if (!hasMessageText(update, BotStringConstants.BUY_OR_SELL)) return;
        Long chatId = UpdateUtil.getChatId(update);
        String message = UpdateUtil.getMessageText(update);
        FiatCurrency fiatCurrency = FiatCurrency.getByCode(message);
        userDataService.updateFiatCurrencyByUserChatId(chatId, fiatCurrency);
        responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, BotKeyboard.BUY_OR_SELL);
        modifyUserService.nextStep(chatId);
    }
}
