package tgb.btc.rce.service.enums.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.service.bean.bot.user.ModifyUserService;
import tgb.btc.library.service.bean.bot.user.ReadUserService;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.handler.util.impl.handler.StartService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeRoleServiceTest {

    @Mock
    private ResponseSender responseSender;

    @Mock
    private ReadUserService readUserService;

    @Mock
    private ModifyUserService modifyUserService;

    @Mock
    private StartService startService;

    @InjectMocks
    private ChangeRoleService changeRoleService;

    @Test
    @DisplayName("Должен запросить ввод chat id.")
    protected void changeRoleNoChatId() {
        Message message = new Message();
        message.setText("/makeadmin");
        Chat chat = new Chat();
        Long chatId = 12345678L;
        chat.setId(chatId);
        message.setChat(chat);
        UserRole userRole = UserRole.ADMIN;
        changeRoleService.changeRole(message, userRole);
        verify(responseSender, times(1)).sendMessage(chatId, """
                    Введите chatId пользователя после команды. Пример:
                    /makeuser 12345678
                    """);
    }

    @Test
    @DisplayName("Должен сообщить о неверном формате chat id.")
    protected void changeRoleWrongFormatChatId() {
        Message message = new Message();
        message.setText("/makeadmin qwe");
        Chat chat = new Chat();
        Long chatId = 12345678L;
        chat.setId(chatId);
        message.setChat(chat);
        UserRole userRole = UserRole.ADMIN;
        changeRoleService.changeRole(message, userRole);
        verify(responseSender, times(1)).sendMessage(chatId, "Неверный формат chat id.");
    }

    @Test
    @DisplayName("Должен сообщить о том, что пользователь не найден.")
    protected void changeRoleNoUser() {
        Message message = new Message();
        message.setText("/makeadmin 123");
        Chat chat = new Chat();
        Long chatId = 12345678L;
        chat.setId(chatId);
        message.setChat(chat);
        UserRole userRole = UserRole.ADMIN;
        when(readUserService.existsByChatId(123L)).thenReturn(false);
        changeRoleService.changeRole(message, userRole);
        verify(responseSender, times(1)).sendMessage(chatId, "Пользователь с таким ID не найден.");
    }

    @Test
    @DisplayName("Должен сообщить о том, что пользователь уже находится в этой роли.")
    protected void changeRoleToSame() {
        Message message = new Message();
        message.setText("/makeadmin 123");
        Chat chat = new Chat();
        Long chatId = 12345678L;
        chat.setId(chatId);
        message.setChat(chat);
        UserRole userRole = UserRole.ADMIN;
        when(readUserService.existsByChatId(123L)).thenReturn(true);
        when(readUserService.getUserRoleByChatId(123L)).thenReturn(UserRole.ADMIN);
        changeRoleService.changeRole(message, userRole);
        verify(responseSender, times(1)).sendMessage(chatId, "Пользователь <b>123</b> уже в роли \"Администратор\".");
    }

    @Test
    @DisplayName("Должен сменить роль на администратор.")
    protected void changeRoleToAdmin() {
        Message message = new Message();
        Long userChatId = 123L;
        message.setText("/makeadmin " + userChatId);
        Chat chat = new Chat();
        Long chatId = 12345678L;
        chat.setId(chatId);
        message.setChat(chat);
        UserRole userRole = UserRole.ADMIN;
        when(readUserService.existsByChatId(userChatId)).thenReturn(true);
        when(readUserService.getUserRoleByChatId(userChatId)).thenReturn(UserRole.USER);
        changeRoleService.changeRole(message, userRole);
        verify(modifyUserService, times(1)).updateUserRoleByChatId(UserRole.ADMIN, userChatId);
        verify(responseSender, times(1)).sendMessage(chatId, "Пользователю " + userChatId + " сменена роль на \"Администратор\".");
        verify(responseSender, times(1)).sendMessage(userChatId, "Вы были переведены в роль \"Администратор\".");
        verify(startService, times(1)).process(userChatId);
    }

    @Test
    @DisplayName("Должен сменить роль на администратор.")
    protected void changeRoleToUser() {
        Message message = new Message();
        Long userChatId = 123L;
        message.setText("/makeuser " + userChatId);
        Chat chat = new Chat();
        Long chatId = 12345678L;
        chat.setId(chatId);
        message.setChat(chat);
        UserRole userRole = UserRole.USER;
        when(readUserService.existsByChatId(userChatId)).thenReturn(true);
        when(readUserService.getUserRoleByChatId(userChatId)).thenReturn(UserRole.ADMIN);
        changeRoleService.changeRole(message, userRole);
        verify(modifyUserService, times(1)).updateUserRoleByChatId(UserRole.USER, userChatId);
        verify(responseSender, times(1)).sendMessage(chatId, "Пользователю " + userChatId + " сменена роль на \"Пользователь\".");
        verify(responseSender, times(1)).sendMessage(userChatId, "Вы были переведены в роль \"Пользователь\".");
        verify(startService, times(1)).process(userChatId);
    }
}