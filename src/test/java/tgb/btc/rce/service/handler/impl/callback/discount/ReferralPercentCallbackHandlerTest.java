package tgb.btc.rce.service.handler.impl.callback.discount;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReferralPercentCallbackHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @Mock
    private IRedisStringService redisStringService;

    @InjectMocks
    private ReferralPercentCallbackHandler handler;

    @Test
    void handle() {
        User user = new User();
        Long chatId = 123456789L;
        user.setId(chatId);
        Message message = new Message();
        Integer messageId = 55000;
        message.setMessageId(messageId);
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setMessage(message);
        callbackQuery.setFrom(user);
        String data = "data";
        callbackQuery.setData(data);
        Long userChatId = 987654321L;
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(userChatId);
        handler.handle(callbackQuery);
        verify(responseSender, times(1)).deleteMessage(chatId, messageId);
        verify(redisUserStateService, times(1)).save(chatId, UserState.NEW_REFERRAL_PERCENT);
        verify(redisStringService, times(1)).save(chatId, userChatId.toString());
        verify(responseSender, times(1)).sendMessage(chatId,
                "Введите новый процент реферальных отчислений для пользователя <b>"
                        + userChatId + "</b>. Для того чтобы у пользователя был общий процент, введите 0.");
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.REFERRAL_PERCENT, handler.getCallbackQueryData());
    }
}