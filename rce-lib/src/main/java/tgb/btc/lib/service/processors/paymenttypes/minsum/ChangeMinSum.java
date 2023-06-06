package tgb.btc.lib.service.processors.paymenttypes.minsum;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.BotKeyboard;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.FiatCurrencyUtil;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.CHANGE_MIN_SUM)
public class ChangeMinSum extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (FiatCurrencyUtil.isFew()) {
            responseSender.sendMessage(chatId, BotStringConstants.FIAT_CURRENCY_CHOOSE, BotKeyboard.FIAT_CURRENCIES);
        } else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, BotKeyboard.BUY_OR_SELL);
            userService.nextStep(chatId, Command.CHANGE_MIN_SUM);
        }
        userService.nextStep(chatId, Command.CHANGE_MIN_SUM);
    }
}
