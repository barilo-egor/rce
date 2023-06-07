package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.BotProperties;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.CryptoCurrency;
import tgb.btc.lib.enums.DealType;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.TurningCurrenciesUtil;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.TURN_ON_CURRENCY)
public class TurnOnCurrencyProcessor extends Processor {

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
            TurningCurrenciesUtil.BUY_TURNING.put(currency, true);
            BotProperties.TURNING_CURRENCIES_PROPERTIES.setProperty("buy." + currency.name(), true);
        } else {
            TurningCurrenciesUtil.SELL_TURNING.put(currency, true);
            BotProperties.TURNING_CURRENCIES_PROPERTIES.setProperty("sell." + currency.name(), true);
        }
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        turningCurrencyProcessor.run(update);
    }
}
