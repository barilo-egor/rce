package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.service.properties.CurrenciesTurningPropertiesReader;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.ITurningCurrencyService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.ITurningCurrenciesService;

@Service
public class TurnOffCurrencyHandler implements ICallbackQueryHandler {

    private final ITurningCurrenciesService turningCurrenciesService;

    private final CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader;

    private final IResponseSender responseSender;

    private final ITurningCurrencyService turningCurrencyService;

    private final ICallbackDataService callbackDataService;

    public TurnOffCurrencyHandler(ITurningCurrenciesService turningCurrenciesService,
                                  CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader,
                                  IResponseSender responseSender, ITurningCurrencyService turningCurrencyService,
                                  ICallbackDataService callbackDataService) {
        this.turningCurrenciesService = turningCurrenciesService;
        this.currenciesTurningPropertiesReader = currenciesTurningPropertiesReader;
        this.responseSender = responseSender;
        this.turningCurrencyService = turningCurrencyService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        DealType dealType = DealType.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        CryptoCurrency currency = CryptoCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 2));

        if (DealType.BUY.equals(dealType)) {
            turningCurrenciesService.addBuy(currency, false);
            currenciesTurningPropertiesReader.setProperty("buy." + currency.name(), false);
        } else {
            turningCurrenciesService.addSell(currency, false);
            currenciesTurningPropertiesReader.setProperty("sell." + currency.name(), false);
        }
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        turningCurrencyService.process(chatId);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.TURN_OFF_CURRENCY;
    }
}
