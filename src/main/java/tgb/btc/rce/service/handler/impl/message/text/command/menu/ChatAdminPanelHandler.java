package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class ChatAdminPanelHandler implements ITextCommandHandler {

    public static final String MESSAGE = "Вы перешли в панель администратора чата.";

    private final IMenuSender menuSender;

    public ChatAdminPanelHandler(IMenuSender menuSender) {
        this.menuSender = menuSender;
    }

    @Override
    public void handle(Message message) {
        menuSender.send(message.getChatId(), MESSAGE, Menu.CHAT_ADMIN_PANEL);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.CHAT_ADMIN_PANEL;
    }
}
