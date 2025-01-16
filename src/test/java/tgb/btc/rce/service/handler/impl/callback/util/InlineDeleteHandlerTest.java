package tgb.btc.rce.service.handler.impl.callback.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InlineDeleteHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private InlineDeleteHandler handler;

    @Test
    @DisplayName("Должен удалить только сообщение самого callback query.")
    void handleWithoutArguments() {
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

        when(callbackDataService.hasArguments(data)).thenReturn(false);

        handler.handle(callbackQuery);

        verify(responseSender).deleteMessage(chatId, messageId);
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(Set.of(1)),
                Arguments.of(Set.of(124,1555)),
                Arguments.of(Set.of(1525525,142412445,656457851))
        );
    }

    @ParameterizedTest
    @MethodSource("getArguments")
    void handleWithArguments(Set<Integer> messageIds) {
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

        when(callbackDataService.hasArguments(data)).thenReturn(true);
        when(callbackDataService.getIntArguments(data)).thenReturn(messageIds);

        handler.handle(callbackQuery);

        for (Integer id : messageIds) {
            verify(responseSender).deleteMessage(chatId, id);
        }
        verify(responseSender).deleteMessage(chatId, messageId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.INLINE_DELETE, handler.getCallbackQueryData());
    }
}