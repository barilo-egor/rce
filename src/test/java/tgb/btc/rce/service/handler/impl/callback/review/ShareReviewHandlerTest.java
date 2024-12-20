package tgb.btc.rce.service.handler.impl.callback.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.rce.enums.RedisPrefix;
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
class ShareReviewHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @Mock
    private IRedisStringService redisStringService;

    @Spy
    private IKeyboardService keyboardService;

    @Mock
    private ICallbackDataService callbackDataService;

    @InjectMocks
    private ShareReviewHandler shareReviewHandler;

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

        Long dealPid = 12333L;
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(dealPid);

        shareReviewHandler.handle(callbackQuery);
        verify(redisStringService).save(RedisPrefix.MESSAGE_ID, chatId, messageId.toString());
        verify(redisStringService).save(RedisPrefix.DEAL_PID, chatId, dealPid.toString());
        verify(redisUserStateService).save(chatId, UserState.SHARE_REVIEW);
    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.SHARE_REVIEW, shareReviewHandler.getCallbackQueryData());
    }
}