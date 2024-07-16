package tgb.btc.rce.service.processors.admin.settings.currencies;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.ITurningCurrenciesService;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.TURN_OFF_CURRENCY)
public class TurnOffCurrencyProcessor extends Processor {

    private TurningCurrencyProcessor turningCurrencyProcessor;

    private ITurningCurrenciesService turningCurrenciesService;

    @Autowired
    public void setTurningCurrenciesService(ITurningCurrenciesService turningCurrenciesService) {
        this.turningCurrenciesService = turningCurrenciesService;
    }

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
            turningCurrenciesService.addBuy(currency, false);
            PropertiesPath.CURRENCIES_TURNING_PROPERTIES.setProperty("buy." + currency.name(), false);
        } else {
            turningCurrenciesService.addSell(currency, false);
            PropertiesPath.CURRENCIES_TURNING_PROPERTIES.setProperty("sell." + currency.name(), false);
        }
        responseSender.deleteMessage(updateService.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        turningCurrencyProcessor.run(update);
    }
}
