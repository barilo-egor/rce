package tgb.btc.rce.service.processors.admin.settings.paymenttypes.turning;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.util.FiatCurrencyUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.TURN_PAYMENT_TYPES)
public class TurnPaymentTypes extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (FiatCurrencyUtil.isFew()) {
            responseSender.sendMessage(chatId, BotStringConstants.FIAT_CURRENCY_CHOOSE, keyboardService.getFiatCurrenciesKeyboard());
        } else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, keyboardService.getBuyOrSell());
            modifyUserService.nextStep(chatId, Command.TURN_PAYMENT_TYPES.name());
        }
        modifyUserService.nextStep(chatId, Command.TURN_PAYMENT_TYPES.name());
    }

}
