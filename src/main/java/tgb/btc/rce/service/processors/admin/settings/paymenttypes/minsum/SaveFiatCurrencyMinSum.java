package tgb.btc.rce.service.processors.admin.settings.paymenttypes.minsum;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.CHANGE_MIN_SUM, step = 1)
public class SaveFiatCurrencyMinSum extends Processor {

    private IUserDataService userDataService;

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        userDataService.updateFiatCurrencyByUserChatId(chatId, FiatCurrency.getByCode(updateService.getMessageText(update)));
        responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, keyboardService.getBuyOrSell());
        modifyUserService.nextStep(chatId, Command.CHANGE_MIN_SUM.name());
    }
}
