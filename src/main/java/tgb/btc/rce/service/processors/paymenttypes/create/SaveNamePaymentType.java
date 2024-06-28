package tgb.btc.rce.service.processors.paymenttypes.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.library.util.FiatCurrencyUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE, step = 1)
public class SaveNamePaymentType extends Processor {

    private IUserDataService userDataService;

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!hasMessageText(update, NewPaymentType.ENTER_NAME)) return;
        String newTypeName = UpdateUtil.getMessageText(update);
        userDataService.updateStringByUserChatId(chatId, newTypeName);
        if (FiatCurrencyUtil.isFew()) {
            responseSender.sendMessage(chatId, BotStringConstants.FIAT_CURRENCY_CHOOSE, BotKeyboard.FIAT_CURRENCIES);
            modifyUserService.nextStep(chatId);
        } else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, BotKeyboard.BUY_OR_SELL);
            modifyUserService.nextStep(chatId);
            modifyUserService.nextStep(chatId);
        }
    }

}
