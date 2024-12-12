package tgb.btc.rce.service.handler.impl.callback.discount;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalBuyDiscountCallbackHandlerTest {

    @Mock
    private ICallbackDataService callbackDataService;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @Mock
    private IRedisStringService redisStringService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IKeyboardService keyboardService;

    @InjectMocks
    private PersonalBuyDiscountCallbackHandler handler;

    @Test
    void handle() {
        CallbackQuery callbackQuery = new CallbackQuery();
        Message message = new Message();
        Integer messageId = 10000;
        message.setMessageId(messageId);
        callbackQuery.setMessage(message);
        User user = new User();
        callbackQuery.setFrom(user);
        Long chatId = 987654321L;
        user.setId(chatId);
        String data = "data";
        callbackQuery.setData(data);
        Long userChatId = 123456789L;
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();
        when(callbackDataService.getLongArgument(data, 1)).thenReturn(userChatId);
        when(keyboardService.getReplyCancel()).thenReturn(replyKeyboard);
        handler.handle(callbackQuery);
        verify(responseSender, times(1)).deleteMessage(chatId, messageId);
        verify(redisStringService, times(1)).save(chatId, userChatId.toString());
        verify(redisUserStateService, times(1)).save(chatId, UserState.NEW_PERSONAL_BUY);
        verify(responseSender, times(1)).sendMessage(chatId,
                "Введите новое значение персональной скидки на покупку: " +
                "<b>положительное</b> для скидки, <b>отрицательное</b> для надбавки.", replyKeyboard);

    }

    @Test
    void getCallbackQueryData() {
        assertEquals(CallbackQueryData.PERSONAL_BUY_DISCOUNT, handler.getCallbackQueryData());
    }
}