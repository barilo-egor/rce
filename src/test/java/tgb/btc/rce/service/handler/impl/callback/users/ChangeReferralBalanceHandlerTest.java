package tgb.btc.rce.service.handler.impl.callback.users;

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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangeReferralBalanceHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IKeyboardService keyboardService;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IRedisStringService redisStringService;

    @InjectMocks
    private ChangeReferralBalanceHandler handler;

    @ParameterizedTest
    @ValueSource(longs = {1, 22005, 5554442})
    void handle(long userChatId) {
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

        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();

        when(callbackDataService.getLongArgument(data, 1)).thenReturn(userChatId);
        when(keyboardService.getReplyCancel()).thenReturn(replyKeyboard);

        handler.handle(callbackQuery);

        verify(redisStringService).save(chatId, String.valueOf(userChatId));
        verify(responseSender).sendMessage(chatId, """
                        Введите новую сумму для пользователя.
                        Для того, чтобы полностью заменить значение, отправьте новое число без знаков. Пример:
                        1750
                        
                        Чтобы добавить к текущему значению баланса сумму, отправьте число со знаком "+". Пример:
                        +1750
                        
                        Чтобы отнять от текущего значения баланса сумму, отправьте число со знаком "-". Пример:
                        -1750""", replyKeyboard);
        verify(redisUserStateService).save(chatId, UserState.CHANGE_REFERRAL_BALANCE);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.CHANGE_REFERRAL_BALANCE, handler.getCallbackQueryData());
    }
}