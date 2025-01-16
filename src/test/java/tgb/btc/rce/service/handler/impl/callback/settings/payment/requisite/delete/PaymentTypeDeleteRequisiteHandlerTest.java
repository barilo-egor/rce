package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.delete;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.util.IShowRequisitesService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentTypeDeleteRequisiteHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IShowRequisitesService showRequisitesService;

    @InjectMocks
    private PaymentTypeDeleteRequisiteHandler paymentTypeDeleteRequisiteHandler;

    @ParameterizedTest
    @ValueSource(longs = {0, 10052, 6656666432L})
    void handle(Long paymentTypePid) {
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

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(paymentTypePid);

        paymentTypeDeleteRequisiteHandler.handle(callbackQuery);

        verify(showRequisitesService).showForDelete(chatId, paymentTypePid, messageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.PAYMENT_TYPE_DELETE_REQUISITE, paymentTypeDeleteRequisiteHandler.getCallbackQueryData());
    }
}