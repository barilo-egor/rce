package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import tgb.btc.library.exception.BaseException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateUtilTest {

    private final int numberOfTestUpdates = 3;

    private List<User> users = new ArrayList<>();

    private List<Chat> chats = new ArrayList<>();

    @BeforeAll
    void init() {
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            long randomChatId = RandomUtils.nextLong(0, 99999999);

            User user = new User();
            user.setId(randomChatId);
            user.setUserName(RandomStringUtils.randomAlphanumeric(10));
            users.add(user);

            Chat chat = new Chat();
            chat.setId(randomChatId);
            chats.add(chat);
        }
        users = Collections.unmodifiableList(users);
        chats = Collections.unmodifiableList(chats);
    }

    @Test
    void getChatIdFromMessage() {
        Map<Long, Update> updates = new HashMap<>();
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            Message message = new Message();
            User user = users.get(i - 1);
            message.setFrom(user);
            Update update = new Update();
            update.setMessage(message);
            updates.put(user.getId(), update);
        }
        for (Map.Entry<Long, Update> updateEntry : updates.entrySet()) {
            assertEquals(updateEntry.getKey(), UpdateUtil.getChatId(updateEntry.getValue()), "Неверный chatid из update message.");
        }
    }

    @Test
    void getChatIdFromCallbackQuery() {
        Map<Long, Update> updates = new HashMap<>();
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            CallbackQuery callbackQuery = new CallbackQuery();
            User user = users.get(i - 1);
            callbackQuery.setFrom(user);
            Update update = new Update();
            update.setCallbackQuery(callbackQuery);
            updates.put(user.getId(), update);
        }
        for (Map.Entry<Long, Update> updateEntry : updates.entrySet()) {
            assertEquals(updateEntry.getKey(), UpdateUtil.getChatId(updateEntry.getValue()), "Неверный chatid из update callbackQuery.");
        }
    }

    @Test
    void getChatIdFromInlineQuery() {
        Map<Long, Update> updates = new HashMap<>(numberOfTestUpdates);
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            InlineQuery inlineQuery = new InlineQuery();
            User user = users.get(i - 1);
            inlineQuery.setFrom(user);
            Update update = new Update();
            update.setInlineQuery(inlineQuery);
            updates.put(user.getId(), update);
        }
        for (Map.Entry<Long, Update> updateEntry : updates.entrySet()) {
            assertEquals(updateEntry.getKey(), UpdateUtil.getChatId(updateEntry.getValue()), "Неверный chatid из update inlineQuery.");
        }
    }

    @Test
    void getChatIdThrowsBaseException() {
        Update update = new Update();
        update.setChannelPost(new Message());
        assertThrows(BaseException.class, () -> UpdateUtil.getChatId(update), "Не было брошено исключение при пустом update.");
    }

    @Test
    void getGroupChatIdFromMyChatMember() {
        Map<Long, Update> updates = new HashMap<>(numberOfTestUpdates);
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
            Chat chat = chats.get(i - 1);
            chatMemberUpdated.setChat(chat);
            Update update = new Update();
            update.setMyChatMember(chatMemberUpdated);
            updates.put(chat.getId(), update);
        }
        for (Map.Entry<Long, Update> updateEntry : updates.entrySet()) {
            assertEquals(updateEntry.getKey(), UpdateUtil.getGroupChatId(updateEntry.getValue()), "Неверный group chatid группы из update myChatMember.");
        }
    }

    @Test
    void getGroupChatIdFromChatMember() {
        Map<Long, Update> updates = new HashMap<>(numberOfTestUpdates);
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
            Chat chat = chats.get(i - 1);
            chatMemberUpdated.setChat(chat);
            Update update = new Update();
            update.setChatMember(chatMemberUpdated);
            updates.put(chat.getId(), update);
        }
        for (Map.Entry<Long, Update> updateEntry : updates.entrySet()) {
            assertEquals(updateEntry.getKey(), UpdateUtil.getGroupChatId(updateEntry.getValue()), "Неверный group chatid группы из update chatMember.");
        }
    }

    @Test
    void getGroupChatIdFromMessage() {
        Map<Long, Update> updates = new HashMap<>(numberOfTestUpdates);
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            Message message = new Message();
            Chat chat = chats.get(i - 1);
            message.setChat(chat);
            Update update = new Update();
            update.setMessage(message);
            updates.put(chat.getId(), update);
        }
        for (Map.Entry<Long, Update> updateEntry : updates.entrySet()) {
            assertEquals(updateEntry.getKey(), UpdateUtil.getGroupChatId(updateEntry.getValue()), "Неверный group chatid группы из update message.");
        }
    }

    @Test
    void getGroupChatIdReturnsNull() {
        Update update = new Update();
        assertNull(UpdateUtil.getGroupChatId(update));
    }

    @Test
    void isGroupMessageByMyChatMemberTypeGroup() {
        assertTrue(UpdateUtil.isGroupMessage(getUpdateWithChatMemberUpdatedWithChat("group")),
                "Для чата с типом group должен вернуться true.");
    }

    @Test
    void isGroupMessageByMyChatMemberTypeSuperGroup() {
        assertTrue(UpdateUtil.isGroupMessage(getUpdateWithChatMemberUpdatedWithChat("supergroup")),
                "Для чата с типом supergroup должен вернуться true.");
    }

    private Update getUpdateWithChatMemberUpdatedWithChat(String type) {
        Chat chat = getChatWithType(type);
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        chatMemberUpdated.setChat(chat);
        Update update = new Update();
        update.setMyChatMember(chatMemberUpdated);
        return update;
    }

    private Chat getChatWithType(String type) {
        Chat chat = new Chat();
        chat.setType(type);
        return chat;
    }

    private Update getUpdateWithMessageChat(String type) {
        Chat chat = getChatWithType(type);
        Message message = new Message();
        message.setChat(chat);
        Update update = new Update();
        update.setMessage(message);
        return update;
    }

    @Test
    void isGroupMessageByMessageChatGroupType() {
        assertTrue(UpdateUtil.isGroupMessage(getUpdateWithMessageChat("group")),
                "Для chat type group в message должно вернуть true.");
    }

    @Test
    void isGroupMessageByMessageChatSuperGroupType() {
        assertTrue(UpdateUtil.isGroupMessage(getUpdateWithMessageChat("supergroup")),
                "Для chat type supergroup в message должно вернуть true.");
    }

    @Test
    void isGroupMessageReturnsFalse() {
        assertFalse(UpdateUtil.isGroupMessage(new Update()));
    }

    @Test
    void getUsername() {
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            Update update = new Update();
            Message message = new Message();
            message.setFrom(users.get(i - 1));
            update.setMessage(message);
            assertEquals(users.get(i - 1).getUserName(), UpdateUtil.getUsername(update));
        }
    }

    @Test
    void getMessageId() {
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            Update update = new Update();
            Message message = new Message();
            Integer randomMessageId = RandomUtils.nextInt(0, 999999);
            message.setMessageId(randomMessageId);
            update.setMessage(message);
            assertEquals(randomMessageId, UpdateUtil.getMessageId(update));
        }
    }

    @Test
    void getMessageIdThrows() {
        assertThrows(BaseException.class, () -> UpdateUtil.getMessageId(new Update()));
    }

    @Test
    void hasMessageTextWithoutMessage() {
        Update update = new Update();
        assertFalse(UpdateUtil.hasMessageText(update));
    }

    @Test
    void hasMessageTextWithoutText() {
        Update update = new Update();
        update.setMessage(new Message());
        assertFalse(UpdateUtil.hasMessageText(update));
    }

    @Test
    void hasMessage() {
        Update update = new Update();
        Message message = new Message();
        message.setText("qwerty");
        update.setMessage(message);
        assertTrue(UpdateUtil.hasMessageText(update));
    }

    @Test
    void getMessageText() {
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            Update update = new Update();
            Message message = new Message();
            String messageText = RandomStringUtils.randomAlphanumeric(10);
            message.setText(messageText);
            update.setMessage(message);
            assertEquals(messageText, UpdateUtil.getMessageText(update));
        }
    }

    @Test
    void getMessageTextThrows() {
        assertThrows(BaseException.class, () -> UpdateUtil.getMessageText(new Update()));
    }

    @Test
    void getLongFromText() {
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            Long randomLong = RandomUtils.nextLong(0, 1000);
            String strRandom = randomLong.toString();
            Update update = new Update();
            Message message = new Message();
            message.setText(strRandom);
            update.setMessage(message);
            assertEquals(randomLong, UpdateUtil.getLongFromText(update));
        }
    }

    @Test
    void getBigDecimalFromText() {
        for (int i = 1; i <= numberOfTestUpdates; i++) {
            double randomDouble = RandomUtils.nextDouble(0, 1000);
            Update update = new Update();
            Message message = new Message();
            message.setText(Double.toString(randomDouble));
            update.setMessage(message);
            assertEquals(BigDecimal.valueOf(randomDouble), UpdateUtil.getBigDecimalFromText(update));
        }
    }

    @Test
    void getMessageFromUpdate() {
        Update update = new Update();
        Message message = new Message();
        update.setMessage(message);
        assertEquals(message, UpdateUtil.getMessage(update));
    }

    @Test
    void getMessageFromCallbackQuery() {
        Update update = new Update();
        Message message = new Message();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setMessage(message);
        update.setCallbackQuery(callbackQuery);
        assertEquals(message, UpdateUtil.getMessage(update));
    }

    @Test
    void getMessageThrows() {
        Update update = new Update();
        update.setChannelPost(new Message());
        assertThrows(BaseException.class, () -> UpdateUtil.getMessage(update));
    }
}
