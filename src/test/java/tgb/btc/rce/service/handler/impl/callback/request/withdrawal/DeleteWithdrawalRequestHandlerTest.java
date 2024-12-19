package tgb.btc.rce.service.handler.impl.callback.request.withdrawal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteWithdrawalRequestHandlerTest {

    @Mock
    private IWithdrawalRequestService withdrawalRequestService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private DeleteWithdrawalRequestHandler handler;

    @Test
    void handle() {
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

        Long withdrawalRequestId = 53333L;
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(withdrawalRequestId);
        when(withdrawalRequestService.findById(withdrawalRequestId)).thenReturn(withdrawalRequest);

        handler.handle(callbackQuery);

        verify(withdrawalRequestService).delete(withdrawalRequest);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(responseSender).sendMessage(chatId, "Заявка на вывод средств удалена.");
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.DELETE_WITHDRAWAL_REQUEST, handler.getCallbackQueryData());
    }
}