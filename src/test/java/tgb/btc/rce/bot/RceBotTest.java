package tgb.btc.rce.bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RceBotTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void getBotUsername() {
        String username = "someUsernameBot";
        RceBot bot = new RceBot(applicationEventPublisher, username, "token");
        assertEquals(username, bot.getBotUsername());
    }

    @Test
    void onUpdateReceived() {
        RceBot bot = new RceBot(applicationEventPublisher, null, null);
        Update update = new Update();
        ArgumentCaptor<TelegramUpdateEvent> telegramUpdateEventListenerArgumentCaptor
                = ArgumentCaptor.forClass(TelegramUpdateEvent.class);
        bot.onUpdateReceived(update);
        verify(applicationEventPublisher, times(1))
                .publishEvent(telegramUpdateEventListenerArgumentCaptor.capture());
        assertEquals(update, telegramUpdateEventListenerArgumentCaptor.getValue().getUpdate());
        assertEquals(bot, telegramUpdateEventListenerArgumentCaptor.getValue().getSource());
    }
}