package tgb.btc.rce.sender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.service.bean.bot.user.ReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.impl.util.MenuService;
import tgb.btc.rce.service.impl.util.MessagePropertiesService;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MenuSenderTest {

    @Mock
    private ResponseSender responseSender;

    @Mock
    private MenuService menuService;

    @Mock
    private ReadUserService readUserService;

    @Mock
    private MessagePropertiesService messagePropertiesService;

    @InjectMocks
    private MenuSender menuSender;

    @Test
    protected void sendPropertiesMessage() {
        Long chatId = 123456789L;
        PropertiesMessage propertiesMessage = PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE;
        Menu menu = Menu.MAIN;
        String message = "message";
        UserRole userRole = UserRole.USER;
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();
        when(readUserService.getUserRoleByChatId(chatId)).thenReturn(userRole);
        when(menuService.build(menu, userRole)).thenReturn(replyKeyboard);
        when(messagePropertiesService.getMessage(propertiesMessage)).thenReturn(message);
        menuSender.send(chatId, propertiesMessage, menu);
        verify(responseSender, times(1)).sendMessage(chatId, message, replyKeyboard);
    }

    @Test
    protected void send() {
        Long chatId = 123456789L;
        Menu menu = Menu.MAIN;
        String message = "message";
        UserRole userRole = UserRole.USER;
        ReplyKeyboard replyKeyboard = new InlineKeyboardMarkup();
        when(readUserService.getUserRoleByChatId(chatId)).thenReturn(userRole);
        when(menuService.build(menu, userRole)).thenReturn(replyKeyboard);
        menuSender.send(chatId, message, menu);
        verify(responseSender, times(1)).sendMessage(chatId, message, replyKeyboard);
    }

}