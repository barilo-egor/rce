package tgb.btc.rce.service.handler.impl.message.text.command.discount;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.redis.IRedisUserStateService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonalBuyDiscountHandlerTest {

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IRedisUserStateService redisUserStateService;

    @Mock
    private IKeyboardService keyboardService;

    @InjectMocks
    private PersonalBuyDiscountHandler handler;

    @Test
    void handle() {
        Message message = new Message();
        Long chatId = 123456789L;
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        String text = "text";
        message.setText(text);

        ReplyKeyboard replyKeyboard = new ReplyKeyboardMarkup();

        when(keyboardService.getReplyCancel()).thenReturn(replyKeyboard);

        handler.handle(message);

        verify(responseSender).sendMessage(chatId,
                "Введите chat id пользователя, которому хотите сменить персональную скидку на покупку.", replyKeyboard);
        verify(redisUserStateService).save(chatId, UserState.PERSONAL_BUY_CHAT_ID);
    }

    @Test
    void getTextCommand() {
        assertEquals(TextCommand.PERSONAL_BUY_DISCOUNT, handler.getTextCommand());
    }
    
}