package tgb.btc.rce.enums.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;

import static org.junit.jupiter.api.Assertions.*;

class UpdateTypeTest {

    private final Long chatId = 123456789L;

    private Chat chat;

    private User user;

    @BeforeEach
    protected void setUp() {
        chat = new Chat();
        chat.setId(chatId);
        user = new User();
        user.setId(chatId);
    }

    @Test
    void fromUpdateShouldReturnMessage() {
        Update update = new Update();
        Message message = new Message();
        update.setMessage(message);
        assertEquals(UpdateType.MESSAGE, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnInlineQuery() {
        Update update = new Update();
        InlineQuery inlineQuery = new InlineQuery();
        update.setInlineQuery(inlineQuery);
        assertEquals(UpdateType.INLINE_QUERY, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnCallbackQuery() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        update.setCallbackQuery(callbackQuery);
        assertEquals(UpdateType.CALLBACK_QUERY, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnEditedMessage() {
        Update update = new Update();
        Message message = new Message();
        update.setEditedMessage(message);
        assertEquals(UpdateType.EDITED_MESSAGE, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnChannelPost() {
        Update update = new Update();
        Message message = new Message();
        update.setChannelPost(message);
        assertEquals(UpdateType.CHANNEL_POST, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnEditedChannelPost() {
        Update update = new Update();
        Message message = new Message();
        update.setEditedChannelPost(message);
        assertEquals(UpdateType.EDITED_CHANNEL_POST, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnShippingQuery() {
        Update update = new Update();
        ShippingQuery shippingQuery = new ShippingQuery();
        update.setShippingQuery(shippingQuery);
        assertEquals(UpdateType.SHIPPING_QUERY, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnPreCheckoutQuery() {
        Update update = new Update();
        PreCheckoutQuery preCheckoutQuery = new PreCheckoutQuery();
        update.setPreCheckoutQuery(preCheckoutQuery);
        assertEquals(UpdateType.PRE_CHECKOUT_QUERY, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnPoll() {
        Update update = new Update();
        Poll poll = new Poll();
        update.setPoll(poll);
        assertEquals(UpdateType.POLL, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnPollAnswer() {
        Update update = new Update();
        PollAnswer pollAnswer = new PollAnswer();
        update.setPollAnswer(pollAnswer);
        assertEquals(UpdateType.POLL_ANSWER, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnMyChatMember() {
        Update update = new Update();
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        update.setMyChatMember(chatMemberUpdated);
        assertEquals(UpdateType.MY_CHAT_MEMBER, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnChatMember() {
        Update update = new Update();
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        update.setChatMember(chatMemberUpdated);
        assertEquals(UpdateType.CHAT_MEMBER, UpdateType.fromUpdate(update));
    }

    @Test
    void fromUpdateShouldReturnChatJoinRequest() {
        Update update = new Update();
        ChatJoinRequest chatJoinRequest = new ChatJoinRequest();
        update.setChatJoinRequest(chatJoinRequest);
        assertEquals(UpdateType.CHAT_JOIN_REQUEST, UpdateType.fromUpdate(update));
    }

    @Test
    void shouldThrowException() {
        Update update = new Update();
        assertThrows(RuntimeException.class, () -> UpdateType.fromUpdate(update));
    }

    @Test
    void getChatIdFromMessage() {
        Update update = new Update();
        Message message = new Message();
        message.setChat(chat);
        update.setMessage(message);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromInlineQuery() {
        Update update = new Update();
        InlineQuery inlineQuery = new InlineQuery();
        inlineQuery.setFrom(user);
        update.setInlineQuery(inlineQuery);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromCallbackQuery() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setFrom(user);
        update.setCallbackQuery(callbackQuery);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromEditedMessage() {
        Update update = new Update();
        Message message = new Message();
        message.setChat(chat);
        update.setEditedMessage(message);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromChannelPost() {
        Update update = new Update();
        Message message = new Message();
        message.setChat(chat);
        update.setChannelPost(message);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromEditedChannelPost() {
        Update update = new Update();
        Message message = new Message();
        message.setChat(chat);
        update.setEditedChannelPost(message);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromShippingQuery() {
        Update update = new Update();
        ShippingQuery shippingQuery = new ShippingQuery();
        shippingQuery.setFrom(user);
        update.setShippingQuery(shippingQuery);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromPreCheckoutQuery() {
        Update update = new Update();
        PreCheckoutQuery preCheckoutQuery = new PreCheckoutQuery();
        preCheckoutQuery.setFrom(user);
        update.setPreCheckoutQuery(preCheckoutQuery);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromPoll() {
        Update update = new Update();
        Poll poll = new Poll();
        update.setPoll(poll);
        assertNull(UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromPollAnswer() {
        Update update = new Update();
        PollAnswer pollAnswer = new PollAnswer();
        update.setPollAnswer(pollAnswer);
        assertNull(UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromMyChatMember() {
        Update update = new Update();
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        chatMemberUpdated.setChat(chat);
        update.setMyChatMember(chatMemberUpdated);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromChatMember() {
        Update update = new Update();
        ChatMemberUpdated chatMemberUpdated = new ChatMemberUpdated();
        chatMemberUpdated.setChat(chat);
        update.setChatMember(chatMemberUpdated);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void getChatIdFromChatJoinRequest() {
        Update update = new Update();
        ChatJoinRequest chatJoinRequest = new ChatJoinRequest();
        chatJoinRequest.setChat(chat);
        update.setChatJoinRequest(chatJoinRequest);
        assertEquals(chatId, UpdateType.getChatId(update));
    }

    @Test
    void shouldReturnNull() {
        Update update = new Update();
        assertNull(UpdateType.getChatId(update));
    }
}