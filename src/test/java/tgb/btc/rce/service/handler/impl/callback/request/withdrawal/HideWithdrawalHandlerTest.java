package tgb.btc.rce.service.handler.impl.callback.request.withdrawal;

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
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HideWithdrawalHandlerTest {

    @Mock
    private IWithdrawalRequestService withdrawalRequestService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private HideWithdrawalHandler hideWithdrawalHandler;

    @ParameterizedTest
    @ValueSource(longs = {1, 2000, 6024644})
    void handle(Long requestPid) {
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

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(requestPid);
        hideWithdrawalHandler.handle(callbackQuery);
        verify(withdrawalRequestService).updateIsActiveByPid(false, requestPid);
        verify(responseSender).deleteMessage(chatId, messageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.HIDE_WITHDRAWAL, hideWithdrawalHandler.getCallbackQueryData());
    }
}