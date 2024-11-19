package tgb.btc.rce.service.processors.admin.settings.currencies;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.service.properties.CurrenciesTurningPropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.handler.util.ITurningCurrencyService;
import tgb.btc.rce.service.util.ITurningCurrenciesService;


@CommandProcessor(command = Command.TURN_OFF_CURRENCY)
public class TurnOffCurrencyProcessor extends Processor {

    private ITurningCurrenciesService turningCurrenciesService;

    private ITurningCurrencyService turningCurrencyService;

    private CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader;

    @Autowired
    public void setCurrenciesTurningPropertiesReader(CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader) {
        this.currenciesTurningPropertiesReader = currenciesTurningPropertiesReader;
    }

    @Autowired
    public void setTurningCurrencyService(ITurningCurrencyService turningCurrencyService) {
        this.turningCurrencyService = turningCurrencyService;
    }

    @Autowired
    public void setTurningCurrenciesService(ITurningCurrenciesService turningCurrenciesService) {
        this.turningCurrenciesService = turningCurrenciesService;
    }

    @Override
    public void run(Update update) {
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        DealType dealType = DealType.valueOf(values[1]);
        CryptoCurrency currency = CryptoCurrency.valueOf(values[2]);

        if (DealType.BUY.equals(dealType)) {
            turningCurrenciesService.addBuy(currency, false);
            currenciesTurningPropertiesReader.setProperty("buy." + currency.name(), false);
        } else {
            turningCurrenciesService.addSell(currency, false);
            currenciesTurningPropertiesReader.setProperty("sell." + currency.name(), false);
        }
        Long chatId = update.getCallbackQuery().getFrom().getId();
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        turningCurrencyService.process(chatId);
    }
}
