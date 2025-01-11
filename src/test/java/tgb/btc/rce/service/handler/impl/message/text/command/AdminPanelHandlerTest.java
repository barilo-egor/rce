package tgb.btc.rce.service.handler.impl.message.text.command;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMenuSender;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminPanelHandlerTest {

    @Mock
    private IMenuSender menuSender;

    @InjectMocks
    private AdminPanelHandler handler;

    @Test
    void handle() {
        Message message = new Message();
        Long chatId = 123456789L;
        Chat chat = new Chat();
        chat.setId(chatId);
        message.setChat(chat);
        String text = "text";
        message.setText(text);

        handler.handle(message);

        verify(menuSender).send(chatId, PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.ADMIN_PANEL);
    }

    @Test
    void getTextCommand() {
        assertEquals(TextCommand.ADMIN_PANEL, handler.getTextCommand());
    }
}