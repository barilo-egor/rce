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


@CommandProcessor(command = Command.TURN_ON_CURRENCY)
public class TurnOnCurrencyProcessor extends Processor {

    private ITurningCurrencyService turningCurrencyService;

    private ITurningCurrenciesService turningCurrenciesService;

    private CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader;

    @Autowired
    public void setCurrenciesTurningPropertiesReader(CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader) {
        this.currenciesTurningPropertiesReader = currenciesTurningPropertiesReader;
    }

    @Autowired
    public void setTurningCurrenciesService(ITurningCurrenciesService turningCurrenciesService) {
        this.turningCurrenciesService = turningCurrenciesService;
    }

    @Autowired
    public void setTurningCurrencyService(ITurningCurrencyService turningCurrencyService) {
        this.turningCurrencyService = turningCurrencyService;
    }

    @Override
    public void run(Update update) {
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        DealType dealType = DealType.valueOf(values[1]);
        CryptoCurrency currency = CryptoCurrency.valueOf(values[2]);

        if (DealType.BUY.equals(dealType)) {
            turningCurrenciesService.addBuy(currency, true);
            currenciesTurningPropertiesReader.setProperty("buy." + currency.name(), true);
        } else {
            turningCurrenciesService.addSell(currency, true);
            currenciesTurningPropertiesReader.setProperty("sell." + currency.name(), true);
        }
        Long chatId = update.getCallbackQuery().getFrom().getId();
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        turningCurrencyService.process(chatId);
    }
}
