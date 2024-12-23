package tgb.btc.rce.service.handler.impl.callback.settings.payment.turn;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.util.IShowPaymentTypesService;
import tgb.btc.rce.service.impl.util.CallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DealTypeTurnPaymentTypesHandlerTest {

    @Mock
    private CallbackDataService callbackDataService;

    @Mock
    private IShowPaymentTypesService showPaymentTypesService;

    @InjectMocks
    private DealTypeTurnPaymentTypesHandler dealTypeTurnPaymentTypesHandler;

    @ParameterizedTest
    @CsvSource({
            "BUY, BYN",
            "SELL, BYN",
            "BUY, RUB"
    })
    void handle(DealType dealType, FiatCurrency fiatCurrency) {
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

        when(callbackDataService.getArgument(data, 1)).thenReturn(dealType.name());
        when(callbackDataService.getArgument(data, 2)).thenReturn(fiatCurrency.name());

        dealTypeTurnPaymentTypesHandler.handle(callbackQuery);

        verify(showPaymentTypesService).sendForTurn(chatId, dealType, fiatCurrency, messageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DEAL_TYPE_TURN_PAYMENT_TYPES, dealTypeTurnPaymentTypesHandler.getCallbackQueryData());
    }
}