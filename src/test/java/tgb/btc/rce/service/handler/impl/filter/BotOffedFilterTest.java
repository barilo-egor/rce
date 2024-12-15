package tgb.btc.rce.service.handler.impl.filter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.sender.IMessageImageResponseSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BotOffedFilterTest {

    @Mock
    private IMessageImageResponseSender messageImageResponseSender;

    @InjectMocks
    private BotOffedFilter botOffedFilter;

    @Test
    void handle() {
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        Long chatId = 123456789L;
        chat.setId(chatId);
        message.setChat(chat);
        update.setMessage(message);
        botOffedFilter.handle(update);
        verify(messageImageResponseSender, times(1)).sendMessage(MessageImage.BOT_OFF, chatId);
    }

    @Test
    void getType() {
        assertEquals(UpdateFilterType.BOT_OFFED, botOffedFilter.getType());
    }
}