package tgb.btc.rce.service.handler.impl.callback.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.util.ICallbackDataService;

@ExtendWith(MockitoExtension.class)
class BanUnbanCallbackHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private BannedUserCache bannedUserCache;

    @Mock
    private BanningUserService banningUserService;

    @InjectMocks
    private BanUnbanCallbackHandler handler;

    @Test
    @DisplayName("Должен заблокировать пользователя.")
    void handleBan() {
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

        Long userChatId = 987654321L;

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(userChatId);
        when(bannedUserCache.get(userChatId)).thenReturn(false);

        handler.handle(callbackQuery);

        verify(bannedUserCache).get(userChatId);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(banningUserService).ban(userChatId);
        verify(responseSender).sendMessage(chatId, "Пользователь <b>" + userChatId + "</b> заблокирован.");
        verify(banningUserService, times(0)).unban(userChatId);
    }

    @Test
    @DisplayName("Должен разблокировать пользователя.")
    void handleUnban() {
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

        Long userChatId = 987654321L;

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(userChatId);
        when(bannedUserCache.get(userChatId)).thenReturn(true);

        handler.handle(callbackQuery);

        verify(bannedUserCache).get(userChatId);
        verify(responseSender).deleteMessage(chatId, messageId);
        verify(banningUserService).unban(userChatId);
        verify(responseSender).sendMessage(chatId, "Пользователь <b>" + userChatId + "</b> разблокирован.");
        verify(banningUserService, times(0)).ban(userChatId);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.BAN_UNBAN, handler.getCallbackQueryData());
    }
}