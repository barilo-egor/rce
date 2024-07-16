package tgb.btc.rce.service.processors.admin.settings.paymenttypes.delete;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.util.FiatCurrencyUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETE_PAYMENT_TYPE)
public class FiatCurrencyDeletePaymentType extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (FiatCurrencyUtil.isFew()) {
            responseSender.sendMessage(chatId, BotStringConstants.FIAT_CURRENCY_CHOOSE, keyboardService.getFiatCurrencies());
        } else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, keyboardService.getBuyOrSell());
            modifyUserService.nextStep(chatId, Command.DELETE_PAYMENT_TYPE.name());
        }
        modifyUserService.nextStep(chatId, Command.DELETE_PAYMENT_TYPE.name());
    }
}
