package tgb.btc.rce.bot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
    private BannedUserCache bannedUserCache;

    @Mock
    private AntiSpamService antiSpamService;

    @Mock
    private MessagesService messagesService;

    @Mock
    private RedisUserStateService redisUserStateService;

    @Mock
    private UpdateFilterService updateFilterService;

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
    @DisplayName("Должен закончить выполнение метода, если пользователь в бане.")
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

    @Test
    @DisplayName("Должен закончить выполнение метода, если определен спам.")
    protected void shouldReturnWhenSpam() {
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
        when(bannedUserCache.get(chatId)).thenReturn(false);
        when(antiSpamService.isSpam(chatId)).thenReturn(true);
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(redisUserStateService, times(0)).get(any());
        verify(updateFilterService, times(0)).getType(any());
        verify(messagesService, times(0)).sendNoHandler(any());
    }

    @Test
    @DisplayName("Должен обработать UpdateFilter.")
    protected void shouldHandleFilter() {
        List<IUpdateFilter> updateFilters = new ArrayList<>();
        IUpdateFilter handler = Mockito.mock(IUpdateFilter.class);
        when(handler.getType()).thenReturn(UpdateFilterType.START);
        updateFilters.add(handler);
        IUpdateFilter stub = Mockito.mock(IUpdateFilter.class);
        when(stub.getType()).thenReturn(UpdateFilterType.BOT_OFFED);
        updateFilters.add(stub);
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, new ArrayList<>(), new ArrayList<>(), messagesService, updateFilters,
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
        when(updateFilterService.getType(update)).thenReturn(UpdateFilterType.START);
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(handler, times(1)).handle(update);
        verify(redisUserStateService, times(0)).get(any());
        verify(messagesService, times(0)).sendNoHandler(any());
    }

    @Test
    @DisplayName("Должен пропустить обработку UpdateFilter.")
    protected void shouldNotHandleFilter() {
        List<IUpdateFilter> updateFilters = new ArrayList<>();
        IUpdateFilter handler = Mockito.mock(IUpdateFilter.class);
        when(handler.getType()).thenReturn(UpdateFilterType.START);
        updateFilters.add(handler);
        IUpdateFilter stub = Mockito.mock(IUpdateFilter.class);
        when(stub.getType()).thenReturn(UpdateFilterType.BOT_OFFED);
        updateFilters.add(stub);
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, new ArrayList<>(), new ArrayList<>(), messagesService, updateFilters,
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
        when(updateFilterService.getType(update)).thenReturn(UpdateFilterType.GROUP);
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(handler, times(0)).handle(update);
    }

    @Test
    @DisplayName("Должен обработать StateHandler.")
    protected void shouldHandleStateHandler() {
        List<IStateHandler> stateHandlers = new ArrayList<>();
        IStateHandler handler = Mockito.mock(IStateHandler.class);
        when(handler.getUserState()).thenReturn(UserState.ADD_CONTACT);
        stateHandlers.add(handler);
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, new ArrayList<>(), stateHandlers, messagesService, new ArrayList<>(),
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
        when(redisUserStateService.get(chatId)).thenReturn(UserState.ADD_CONTACT);
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(handler, times(1)).handle(update);
        verify(messagesService, times(0)).sendNoHandler(any());
    }

    @Test
    @DisplayName("Должен пропустить обработку StateHandler из-за не поддерживаемого UpdateType.")
    protected void shouldNotHandleStateHandlerCauseOfUpdateType() {
        List<IStateHandler> stateHandlers = new ArrayList<>();
        IStateHandler handler = Mockito.mock(IStateHandler.class);
        when(handler.getUserState()).thenReturn(UserState.ADD_CONTACT);
        stateHandlers.add(handler);
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, new ArrayList<>(), stateHandlers, messagesService, new ArrayList<>(),
                updateFilterService, antiSpamService, bannedUserCache
        );
        Update update = new Update();
        Message message = new Message();
        Chat chat = new Chat();
        Long chatId = 123456789L;
        chat.setId(chatId);
        message.setChat(chat);
        update.setEditedChannelPost(message);
        TelegramUpdateEvent telegramUpdateEvent = new TelegramUpdateEvent(this, update);
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(handler, times(0)).handle(update);
        verify(messagesService, times(1)).sendNoHandler(any());
    }

    @Test
    @DisplayName("Должен пропустить обработку StateHandler из-за отсутствия UserState у пользователя.")
    protected void shouldNotHandleStateHandlerCauseOfNullUserState() {
        List<IStateHandler> stateHandlers = new ArrayList<>();
        IStateHandler handler = Mockito.mock(IStateHandler.class);
        when(handler.getUserState()).thenReturn(UserState.ADD_CONTACT);
        stateHandlers.add(handler);
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, new ArrayList<>(), stateHandlers, messagesService, new ArrayList<>(),
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
        when(redisUserStateService.get(chatId)).thenReturn(null);
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(handler, times(0)).handle(update);
        verify(messagesService, times(1)).sendNoHandler(any());
    }

    @Test
    @DisplayName("Должен пропустить обработку StateHandler из-за отсутствия обработчика.")
    protected void shouldNotHandleStateHandlerCauseOfNoHandler() {
        List<IStateHandler> stateHandlers = new ArrayList<>();
        IStateHandler handler = Mockito.mock(IStateHandler.class);
        when(handler.getUserState()).thenReturn(UserState.ADD_CONTACT);
        stateHandlers.add(handler);
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, new ArrayList<>(), stateHandlers, messagesService, new ArrayList<>(),
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
        when(redisUserStateService.get(chatId)).thenReturn(UserState.DEAL);
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(handler, times(0)).handle(update);
        verify(messagesService, times(1)).sendNoHandler(any());
    }

    @Test
    @DisplayName("Должен обработать UpdateHandler.")
    protected void shouldHandle() {
        List<IUpdateHandler> updateHandlers = new ArrayList<>();
        IUpdateHandler handler = Mockito.mock(IUpdateHandler.class);
        when(handler.getUpdateType()).thenReturn(UpdateType.MESSAGE);
        when(handler.handle(any())).thenReturn(true);
        updateHandlers.add(handler);
        IUpdateHandler stub = Mockito.mock(IUpdateHandler.class);
        when(stub.getUpdateType()).thenReturn(UpdateType.CALLBACK_QUERY);
        updateHandlers.add(stub);
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, updateHandlers, new ArrayList<>(), messagesService, new ArrayList<>(),
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
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(handler, times(1)).handle(update);
        verify(messagesService, times(0)).sendNoHandler(any());
    }

    @Test
    @DisplayName("UpdateFilter должен не обработать update.")
    protected void shouldNotHandle() {
        List<IUpdateHandler> updateHandlers = new ArrayList<>();
        IUpdateHandler handler = Mockito.mock(IUpdateHandler.class);
        when(handler.getUpdateType()).thenReturn(UpdateType.INLINE_QUERY);
        updateHandlers.add(handler);
        IUpdateHandler stub = Mockito.mock(IUpdateHandler.class);
        when(stub.getUpdateType()).thenReturn(UpdateType.CALLBACK_QUERY);
        updateHandlers.add(stub);
        TelegramUpdateEventListener telegramUpdateEventListener = new TelegramUpdateEventListener(
                redisUserStateService, updateHandlers, new ArrayList<>(), messagesService, new ArrayList<>(),
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
        telegramUpdateEventListener.update(telegramUpdateEvent);
        verify(handler, times(0)).handle(update);
        verify(messagesService, times(1)).sendNoHandler(any());
    }
}