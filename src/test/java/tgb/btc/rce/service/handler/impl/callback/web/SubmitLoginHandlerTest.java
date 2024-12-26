package tgb.btc.rce.service.handler.impl.callback.web;

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
import tgb.btc.api.bot.WebAPI;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmitLoginHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private WebAPI webAPI;

    @InjectMocks
    private SubmitLoginHandler handler;

    @ParameterizedTest
    @ValueSource(longs = {1,64363,214124426})
    void handle(Long submitChatId) {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        User user = new User();

        String data = "data";
        Integer messageId = 50000;
        int callbackQueryId = 24005;
        user.setId(submitChatId);
        message.setMessageId(messageId);
        callbackQuery.setData(data);
        callbackQuery.setFrom(user);
        callbackQuery.setMessage(message);
        callbackQuery.setId(Integer.toString(callbackQueryId));

        handler.handle(callbackQuery);

        verify(webAPI).submitLogin(submitChatId);
        verify(responseSender).deleteMessage(submitChatId, messageId);
        verify(responseSender).sendMessage(submitChatId, "Вы можете, если потребуется, закрыть сессию по кнопке ниже.",
                InlineButton.builder().text("Закрыть сессию").data(CallbackQueryData.LOGOUT.name()).build());
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SUBMIT_LOGIN, handler.getCallbackQueryData());
    }
}