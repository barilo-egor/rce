package tgb.btc.rce.service.handler.impl.callback.users;

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
import tgb.btc.rce.enums.BotSystemMessage;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendMessageToUserCallbackHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @InjectMocks
    private SendMessageToUserCallbackHandler handler;

    @ParameterizedTest
    @CsvSource({
            "1, q",
            "25550, Отдельное сообщение одному пользователю.",
            "555004444, Отдельное очень очень очень очень очень очень очень очень очень очень очень очень очень очень " +
                    "очень очень очень очень очень очень большое сообщение.\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89"
    })
    void handle(Long userChatId, String messageText) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        Long chatId = 123456789L;
        int callbackQueryId = 24005;
        user.setId(chatId);
        message.setMessageId(messageId);
        message.setText(messageText);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(userChatId);

        handler.handle(callbackQuery);

        verify(responseSender).sendMessage(userChatId, messageText);
        verify(responseSender).sendEditedMessageText(chatId, messageId, messageText + BotSystemMessage.MESSAGE_SENT.getText());
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SEND_MESSAGE_TO_USER, handler.getCallbackQueryData());
    }
}