package tgb.btc.rce.service.processors.admin.settings.paymenttypes.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.library.util.FiatCurrencyUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.NEW_PAYMENT_TYPE, step = 1)
public class SaveNamePaymentType extends Processor {

    private IUserDataService userDataService;

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (!hasMessageText(update, NewPaymentType.ENTER_NAME)) return;
        String newTypeName = updateService.getMessageText(update);
        userDataService.updateStringByUserChatId(chatId, newTypeName);
        if (FiatCurrencyUtil.isFew()) {
            responseSender.sendMessage(chatId, BotStringConstants.FIAT_CURRENCY_CHOOSE, keyboardService.getFiatCurrenciesKeyboard());
            modifyUserService.nextStep(chatId);
        } else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, keyboardService.getBuyOrSell());
            modifyUserService.nextStep(chatId);
            modifyUserService.nextStep(chatId);
        }
    }

}
