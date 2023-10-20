package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.TurningCurrenciesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.TURN_OFF_CURRENCY)
public class TurnOffCurrencyProcessor extends Processor {

    private TurningCurrencyProcessor turningCurrencyProcessor;

    @Autowired
    public void setTurningCurrencyProcessor(TurningCurrencyProcessor turningCurrencyProcessor) {
        this.turningCurrencyProcessor = turningCurrencyProcessor;
    }

    @Override
    public void run(Update update) {
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        DealType dealType = DealType.valueOf(values[1]);
        CryptoCurrency currency = CryptoCurrency.valueOf(values[2]);

        if (DealType.BUY.equals(dealType)) {
            TurningCurrenciesUtil.BUY_TURNING.put(currency, false);
            CommonProperties.TURNING_CURRENCIES.setProperty("buy." + currency.name(), false);
        } else {
            TurningCurrenciesUtil.SELL_TURNING.put(currency, false);
            CommonProperties.TURNING_CURRENCIES.setProperty("sell." + currency.name(), false);
        }
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        turningCurrencyProcessor.run(update);
    }
}
