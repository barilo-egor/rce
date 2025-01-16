package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.dynamic;

import static org.junit.jupiter.api.Assertions.*;

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
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.util.ITurnDynamicRequisiteService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamicFiatHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private ITurnDynamicRequisiteService turnDynamicRequisiteService;

    @InjectMocks
    private DynamicFiatHandler dynamicFiatHandler;

    @ParameterizedTest
    @EnumSource(FiatCurrency.class)
    void handle(FiatCurrency fiatCurrency) {
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

        when(callbackDataService.getArgument(data, 1)).thenReturn(fiatCurrency.name());

        dynamicFiatHandler.handle(callbackQuery);

        verify(turnDynamicRequisiteService).sendPaymentTypes(chatId, messageId, fiatCurrency);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DYNAMIC_FIAT, dynamicFiatHandler.getCallbackQueryData());
    }
}