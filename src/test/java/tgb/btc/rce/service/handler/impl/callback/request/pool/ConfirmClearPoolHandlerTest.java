package tgb.btc.rce.service.handler.impl.callback.request.pool;

import org.junit.jupiter.api.DisplayName;
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
import tgb.btc.library.exception.ApiResponseErrorException;
import tgb.btc.library.interfaces.web.ICryptoWithdrawalService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmClearPoolHandlerTest {

    @Mock
    private ICryptoWithdrawalService cryptoWithdrawalService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @InjectMocks
    private ConfirmClearPoolHandler confirmClearPoolHandler;

    @ParameterizedTest
    @ValueSource(ints = {1, 50005, 1235532})
    @DisplayName("Должен очистить пул с разными message id.")
    void handle(Integer callbackDataMessageId) {
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

        Message deleteMessage = new Message();
        Integer deleteMessageId = 5005;
        deleteMessage.setMessageId(deleteMessageId);

        when(callbackDataService.getIntArgument(data, 1)).thenReturn(callbackDataMessageId);
        when(responseSender.sendMessage(chatId, "Пул в процессе очищения, пожалуйста подождите.")).thenReturn(Optional.of(deleteMessage));

        confirmClearPoolHandler.handle(callbackQuery);

        verify(cryptoWithdrawalService).clearPool();
        verify(responseSender).deleteMessage(chatId, deleteMessageId);
        verify(responseSender).deleteMessage(chatId, callbackDataMessageId);
        verify(responseSender).deleteMessage(chatId, messageId);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 50005, 1235532})
    @DisplayName("Должен сообщить об ошибке очистки с разными message id.")
    void handleTelegramApiResponseError(Integer callbackDataMessageId) {
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

        Message deleteMessage = new Message();
        Integer deleteMessageId = 5005;
        deleteMessage.setMessageId(deleteMessageId);

        when(responseSender.sendMessage(chatId, "Пул в процессе очищения, пожалуйста подождите.")).thenReturn(Optional.of(deleteMessage));
        String exceptionText = "Some error";
        when(cryptoWithdrawalService.clearPool()).thenThrow(new ApiResponseErrorException(exceptionText));

        confirmClearPoolHandler.handle(callbackQuery);

        verify(responseSender).sendMessage(chatId, exceptionText);
        verify(responseSender).deleteMessage(chatId, deleteMessageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.CONFIRM_CLEAR_POOL, confirmClearPoolHandler.getCallbackQueryData());
    }

}