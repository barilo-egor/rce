package tgb.btc.rce.service.handler.impl.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.*;
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.library.bean.bot.GroupChat;
import tgb.btc.library.bean.web.api.ApiUser;
import tgb.btc.library.constants.enums.MemberStatus;
import tgb.btc.library.constants.enums.bot.GroupChatType;
import tgb.btc.library.interfaces.service.bean.bot.IGroupChatService;
import tgb.btc.library.interfaces.service.bean.web.IApiUserService;
import tgb.btc.rce.enums.UpdateFilterType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IUpdateService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupFilterTest {

    @Mock
    private IGroupChatService groupChatService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private INotificationsAPI notificationsAPI;

    @Mock
    private IUpdateService updateService;

    @Mock
    private IApiUserService apiUserService;

    private GroupFilter groupFilter;

    private final String username = "testbot";

    @BeforeEach
    void setUp() {
        groupFilter = new GroupFilter(groupChatService, responseSender, notificationsAPI, updateService, apiUserService, username);
    }

    @Test
    @DisplayName("Должен прервать выполнение метода, если chat id группы не найден.")
    void shouldReturnIfNoGroupChatId() {
        Update update = new Update();
        Message message = new Message();
        message.setNewChatTitle("title");
        update.setMessage(message);
        when(updateService.getGroupChatId(update)).thenReturn(null);
        groupFilter.handle(update);
        verify(groupChatService, times(0)).updateTitleByChatId(any(), any());
    }

    @Test
    @DisplayName("Должен пропустить обработку MyChatMember, если не совпадает username бота.")
    void shouldSkipMyChatMemberIfNotBotUsername() {
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        User user = new User();
        user.setUserName("anotherUsername");
        ChatMember chatMember = new ChatMemberMember(user);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        groupFilter.handle(update);
        verify(groupChatService, times(0)).deleteIfExistsByChatId(any());
        verify(groupChatService, times(0)).updateIsSendMessageEnabledByChatId(any(), any());
        verify(groupChatService, times(0)).find(any());
    }

    @Test
    @DisplayName("Должен удалить группу запросов по ChatMemberLeft.")
    void shouldDeleteGroupRequestGroupLeftChatMember() {
        Long chatId = 123456789L;
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        User user = new User();
        user.setUserName(username);
        ChatMemberLeft chatMember = new ChatMemberLeft(user);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        List<GroupChat> groupChats = new ArrayList<>(1);
        GroupChat groupChat = new GroupChat();
        groupChat.setChatId(chatId);
        groupChats.add(groupChat);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.getAllByType(GroupChatType.DEAL_REQUEST)).thenReturn(groupChats);
        groupFilter.handle(update);
        verify(groupChatService, times(1)).deleteIfExistsByChatId(chatId);
        verify(notificationsAPI, times(1)).notifyDeletedDealRequestGroup();
        verify(groupChatService, times(0)).find(any());
    }

    @Test
    @DisplayName("Должен удалить группу запросов по ChatMemberKicked.")
    void shouldDeleteGroupRequestGroupKickedChatMember() {
        Long chatId = 123456789L;
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        User user = new User();
        user.setUserName(username);
        ChatMemberBanned chatMember = new ChatMemberBanned(user, 1);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        List<GroupChat> groupChats = new ArrayList<>(1);
        GroupChat groupChat = new GroupChat();
        groupChat.setChatId(chatId);
        groupChats.add(groupChat);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.getAllByType(GroupChatType.DEAL_REQUEST)).thenReturn(groupChats);
        groupFilter.handle(update);
        verify(groupChatService, times(1)).deleteIfExistsByChatId(chatId);
        verify(notificationsAPI, times(1)).notifyDeletedDealRequestGroup();
        verify(groupChatService, times(0)).find(any());
    }

    @Test
    @DisplayName("Должен обновить поле группе к false о возможности отправлять сообщения.")
    void shouldChangeIsMessageEnabledToFalse() {
        Long chatId = 123456789L;
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        User user = new User();
        user.setUserName(username);
        ChatMemberRestricted chatMember = new ChatMemberRestricted(user, null, false,
                null, null, null, null, null,
                null, null, null, null,
                null, null, null, null, null, null);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        groupFilter.handle(update);
        verify(groupChatService, times(1)).updateIsSendMessageEnabledByChatId(false, chatId);
        verify(groupChatService, times(0)).find(chatId);
    }

    @Test
    @DisplayName("Должен обновить поле группе к true о возможности отправлять сообщения.")
    void shouldChangeIsMessageEnabledToTrue() {
        Long chatId = 123456789L;
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        User user = new User();
        user.setUserName(username);
        ChatMemberRestricted chatMember = new ChatMemberRestricted(user, null, true,
                null, null, null, null, null,
                null, null, null, null,
                null, null, null, null, null, null);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        groupFilter.handle(update);
        verify(groupChatService, times(1)).updateIsSendMessageEnabledByChatId(true, chatId);
        verify(groupChatService, times(0)).find(chatId);
    }

    @Test
    @DisplayName("Должен удалить группу запросов апи клиента.")
    void shouldDeleteGroupRequestOfApiUser() {
        Long chatId = 123456789L;
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        User user = new User();
        user.setUserName(username);
        ChatMemberLeft chatMember = new ChatMemberLeft(user);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        List<GroupChat> groupChats = new ArrayList<>(0);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.getAllByType(GroupChatType.DEAL_REQUEST)).thenReturn(groupChats);
        ApiUser apiUser = new ApiUser();
        apiUser.setGroupChat(new GroupChat());
        when(apiUserService.getByGroupChatId(chatId)).thenReturn(apiUser);
        ArgumentCaptor<ApiUser> apiUserArgumentCaptor = ArgumentCaptor.forClass(ApiUser.class);
        groupFilter.handle(update);
        verify(apiUserService).save(apiUserArgumentCaptor.capture());
        assertNull(apiUserArgumentCaptor.getValue().getGroupChat());
        verify(groupChatService, times(1)).deleteIfExistsByChatId(chatId);
        verify(notificationsAPI, times(0)).notifyDeletedDealRequestGroup();
    }

    @Test
    @DisplayName("Должен удалить группу запросов в случае если она не привязана к апи клиенту.")
    void shouldDeleteGroupRequestOfNullApiUser() {
        Long chatId = 123456789L;
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        User user = new User();
        user.setUserName(username);
        ChatMemberLeft chatMember = new ChatMemberLeft(user);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        List<GroupChat> groupChats = new ArrayList<>(0);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.getAllByType(GroupChatType.DEAL_REQUEST)).thenReturn(groupChats);
        when(apiUserService.getByGroupChatId(chatId)).thenReturn(null);
        groupFilter.handle(update);
        verify(apiUserService, times(0)).save(any());
        verify(groupChatService, times(1)).deleteIfExistsByChatId(chatId);
        verify(notificationsAPI, times(0)).notifyDeletedDealRequestGroup();
    }

    @Test
    @DisplayName("Должен обновить статус бота в группе.")
    void shouldUpdateChatMemberStatus() {
        Long chatId = 123456789L;
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        User user = new User();
        user.setUserName(username);
        ChatMemberMember chatMember = new ChatMemberMember(user);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        GroupChat groupChat = new GroupChat();
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.find(chatId)).thenReturn(Optional.of(groupChat));
        groupFilter.handle(update);
        verify(groupChatService, times(1)).updateMemberStatus(chatId, MemberStatus.MEMBER);
    }

    @Test
    @DisplayName("Должен сохранить группу после добавления.")
    void shouldSaveChat() {
        Long chatId = 123456789L;
        Update update = new Update();
        ChatMemberUpdated member = new ChatMemberUpdated();
        Chat chat = new Chat();
        String title = "title";
        chat.setTitle(title);
        member.setChat(chat);
        User user = new User();
        user.setUserName(username);
        ChatMemberMember chatMember = new ChatMemberMember(user);
        member.setNewChatMember(chatMember);
        update.setMyChatMember(member);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.find(chatId)).thenReturn(Optional.empty());
        groupFilter.handle(update);
        verify(groupChatService, times(1)).register(chatId, title, MemberStatus.MEMBER, GroupChatType.DEFAULT);
    }

    @Test
    @DisplayName("Должен обновить title группы.")
    void shouldUpdateTitle() {
        Update update = new Update();
        Message message = new Message();
        String title = "title";
        message.setNewChatTitle(title);
        update.setMessage(message);
        Long chatId = 123456789L;
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        groupFilter.handle(update);
        verify(groupChatService, times(1)).updateTitleByChatId(chatId, title);
    }

    @Test
    @DisplayName("Должен пропустить обновление title, если нет message.")
    void shouldSkipUpdateTitleWithoutMessage() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        update.setCallbackQuery(callbackQuery);
        Long chatId = 123456789L;
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        groupFilter.handle(update);
        verify(groupChatService, times(0)).updateTitleByChatId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Должен пропустить обновление title, если есть текст, но нет newChatTitle.")
    void shouldSkipUpdateTitleWithoutNewChatTitle() {
        Update update = new Update();
        Message message = new Message();
        update.setMessage(message);
        Long chatId = 123456789L;
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        groupFilter.handle(update);
        verify(groupChatService, times(0)).updateTitleByChatId(anyLong(), anyString());
    }

    @Test
    @DisplayName("Должен пропустить обработку, если сообщение не из группы запросов.")
    void shouldSkipIfNotDealRequest() {
        Update update = new Update();
        Message message = new Message();
        message.setText("/help");
        update.setMessage(message);
        Long chatId = 123456789L;
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.isDealRequest(chatId)).thenReturn(false);
        groupFilter.handle(update);
        verify(responseSender, times(0)).sendMessage(any(), any());
    }

    @Test
    @DisplayName("Должен пропустить, если сообщение не ответ на другое сообщение.")
    void shouldSkipIfNotReply() {
        Update update = new Update();
        Message message = new Message();
        message.setText("+");
        update.setMessage(message);
        Long chatId = 123456789L;
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.isDealRequest(chatId)).thenReturn(true);
        groupFilter.handle(update);
        verify(responseSender, times(0)).sendMessage(anyLong(), anyString(), anyInt());
        verify(responseSender, times(0)).sendEditedMessageText(anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Должен пропустить, если сообщение не ответ на другое сообщение бота.")
    void shouldSkipIfNotReplyToBot() {
        Update update = new Update();
        Message message = new Message();
        message.setText("+");
        Message replyMessage = new Message();
        Chat chat = new Chat();
        chat.setId(987654321L);
        replyMessage.setChat(chat);
        message.setReplyToMessage(replyMessage);
        update.setMessage(message);
        Long chatId = 123456789L;
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.isDealRequest(chatId)).thenReturn(true);
        groupFilter.handle(update);
        verify(responseSender, times(0)).sendMessage(anyLong(), anyString(), anyInt());
        verify(responseSender, times(0)).sendEditedMessageText(anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Должен пропустить, если сообщение не равно \"+\".")
    void shouldSkipIfNotPlusMessage() {
        Update update = new Update();
        Message message = new Message();
        message.setText("-");
        Message replyMessage = new Message();
        Chat chat = new Chat();
        Long chatId = 123456789L;
        chat.setId(chatId);
        replyMessage.setChat(chat);
        message.setReplyToMessage(replyMessage);
        update.setMessage(message);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.isDealRequest(chatId)).thenReturn(true);
        groupFilter.handle(update);
        verify(responseSender, times(0)).sendMessage(anyLong(), anyString(), anyInt());
        verify(responseSender, times(0)).sendEditedMessageText(anyLong(), anyInt(), anyString());
    }

    @Test
    @DisplayName("Должен сообщить, что не получилось найти номер сделки.")
    void shouldNotifyIfNotFindDealNumber() {
        Update update = new Update();
        Message message = new Message();
        message.setText("+");
        Integer messageId = 100000;
        message.setMessageId(messageId);
        Message replyMessage = new Message();
        Chat chat = new Chat();
        Long chatId = 123456789L;
        chat.setId(chatId);
        replyMessage.setChat(chat);
        replyMessage.setText("text");
        message.setReplyToMessage(replyMessage);
        update.setMessage(message);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.isDealRequest(chatId)).thenReturn(true);
        groupFilter.handle(update);
        verify(responseSender, times(1)).sendMessage(eq(chatId), anyString(), eq(messageId));
    }

    @Test
    @DisplayName("Должен изменить текст обработанной заявки.")
    void shouldUpdateDealMessage() {
        Update update = new Update();
        Message message = new Message();
        update.setMessage(message);
        message.setText("+");
        Integer messageId = 100000;
        Message replyMessage = new Message();
        Chat chat = new Chat();
        Long chatId = 123456789L;
        chat.setId(chatId);
        replyMessage.setChat(chat);
        replyMessage.setMessageId(messageId);
        replyMessage.setText("Заявка №123\nСумма: 0.001\nДата: 01.01.2020");
        message.setReplyToMessage(replyMessage);
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.isDealRequest(chatId)).thenReturn(true);
        groupFilter.handle(update);
        verify(responseSender, times(1)).sendEditedMessageText(chatId, messageId, "Заявка №123\nОбработано.");
    }

    @Test
    @DisplayName("Должен отправить /help сообщение.")
    void shouldSendHelpMessage() {
        Update update = new Update();
        Message message = new Message();
        message.setText("/help");
        update.setMessage(message);
        Long chatId = 123456789L;
        when(updateService.getGroupChatId(update)).thenReturn(chatId);
        when(groupChatService.isDealRequest(chatId)).thenReturn(true);
        groupFilter.handle(update);
        verify(responseSender, times(1)).sendMessage(chatId, """
                        Чтобы отметить заявку обработанной, \
                        ответьте на сообщение бота с текстом "+". Текст будет заменен на следующий:
                        <blockquote>Заявка №{номер заявки}.
                        Обработано.</blockquote>""");
    }

    @Test
    void getType() {
        assertEquals(UpdateFilterType.GROUP, groupFilter.getType());
    }
}