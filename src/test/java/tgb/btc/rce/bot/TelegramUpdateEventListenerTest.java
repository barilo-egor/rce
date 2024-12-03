package tgb.btc.rce.bot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.exception.HandlerTypeNotFoundException;
import tgb.btc.rce.service.captcha.impl.AntiSpamService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.IUpdateFilter;
import tgb.btc.rce.service.handler.IUpdateHandler;
import tgb.btc.rce.service.handler.util.impl.UpdateFilterService;
import tgb.btc.rce.service.impl.redis.RedisUserStateService;
import tgb.btc.rce.service.processors.support.MessagesService;
import tgb.btc.rce.vo.TelegramUpdateEvent;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramUpdateEventListenerTest {

    @Mock
    public BannedUserCache bannedUserCache;

    @Mock
    public AntiSpamService antiSpamService;

    @Mock
    public MessagesService messagesService;

    @Mock
    public RedisUserStateService redisUserStateService;

    @Mock
    public UpdateFilterService updateFilterService;

    @DisplayName("Должен пробросить исключение отсутствия UpdateType у обработчика апдейта.")
    @Test
    protected void shouldThrowNoUpdateType() {
        List<IUpdateHandler> updateHandlerList = new ArrayList<>();
        updateHandlerList.add(new IUpdateHandler() {
            @Override
            public boolean handle(Update update) {
                return false;
            }

            @Override
            public UpdateType getUpdateType() {
                return null;
            }
        });
        assertThrows(HandlerTypeNotFoundException.class, () -> new TelegramUpdateEventListener(null, updateHandlerList,
                new ArrayList<>(), null, new ArrayList<>(), null, null, null));
    }

    @DisplayName("Должен пробросить исключение отсутствия UserState у обработчика апдейта.")
    @Test
    protected void shouldThrowNoUserState() {
        List<IStateHandler> stateHandlerList = new ArrayList<>();
        stateHandlerList.add(new IStateHandler() {
            @Override
            public void handle(Update update) {

            }

            @Override
            public UserState getUserState() {
                return null;
            }
        });
        assertThrows(HandlerTypeNotFoundException.class, () -> new TelegramUpdateEventListener(null, new ArrayList<>(),
                stateHandlerList, null, new ArrayList<>(), null, null, null));
    }

    @DisplayName("Должен пробросить исключение отсутствия UpdateFilterType у обработчика апдейта.")
    @Test
    protected void shouldThrowNoUpdateFilterType() {
        List<IUpdateFilter> updateFilterHandlerList = new ArrayList<>();
        updateFilterHandlerList.add(new IUpdateFilter() {
            @Override
            public void handle(Update update) {

            }

            @Override
            public UpdateFilterType getType() {
                return null;
            }
        });
        assertThrows(HandlerTypeNotFoundException.class, () -> new TelegramUpdateEventListener(null, new ArrayList<>(),
                new ArrayList<>(), null, updateFilterHandlerList, null, null, null));
    }

    @Test
    protected void shouldReturnWhenBanned() {
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, new ArrayList<>(), new ArrayList<>(), messagesService, new ArrayList<>(),
                updateFilterService, antiSpamService, bannedUserCache
        );
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        Long chatId = 123456789L;
        chat.setId(chatId);
        message.setChat(chat);
        update.setMessage(message);
        TelegramUpdateEvent telegramUpdateEvent = new TelegramUpdateEvent(this, update);
        when(bannedUserCache.get(chatId)).thenReturn(true);
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(antiSpamService, times(0)).isSpam(any());
    }
}