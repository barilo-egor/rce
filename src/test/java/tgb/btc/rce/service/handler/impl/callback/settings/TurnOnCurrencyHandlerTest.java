package tgb.btc.rce.service.handler.impl.callback.settings;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.service.properties.CurrenciesTurningPropertiesReader;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.ITurningCurrencyService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.ITurningCurrenciesService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnOnCurrencyHandlerTest {

    @Mock
    private ITurningCurrenciesService turningCurrenciesService;

    @Mock
    private CurrenciesTurningPropertiesReader currenciesTurningPropertiesReader;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ITurningCurrencyService turningCurrencyService;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private TurnOnCurrencyHandler handler;


    @ParameterizedTest
    @EnumSource(CryptoCurrency.class)
    @DisplayName("Должен включить криптовалюту для покупки.")
    void handleBuy(CryptoCurrency cryptoCurrency) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        when(callbackDataService.getArgument(data, 1)).thenReturn(DealType.BUY.name());
        when(callbackDataService.getArgument(data, 2)).thenReturn(cryptoCurrency.name());

        handler.handle(callbackQuery);

        verify(turningCurrenciesService).addBuy(cryptoCurrency, true);
        verify(currenciesTurningPropertiesReader).setProperty("buy." + cryptoCurrency.name(), true);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(turningCurrencyService).process(chatId);
    }

    @ParameterizedTest
    @EnumSource(CryptoCurrency.class)
    @DisplayName("Должен включить криптовалюту для продажи.")
    void handleSell(CryptoCurrency cryptoCurrency) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        when(callbackDataService.getArgument(data, 1)).thenReturn(DealType.SELL.name());
        when(callbackDataService.getArgument(data, 2)).thenReturn(cryptoCurrency.name());

        handler.handle(callbackQuery);

        verify(turningCurrenciesService).addSell(cryptoCurrency, true);
        verify(currenciesTurningPropertiesReader).setProperty("sell." + cryptoCurrency.name(), true);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(turningCurrencyService).process(chatId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.TURN_ON_CURRENCY, handler.getCallbackQueryData());
    }
}