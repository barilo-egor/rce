package tgb.btc.rce.service.handler.impl.message.text.command.users;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class SendMessagesHandler implements ITextCommandHandler {

    private final IMenuSender menuSender;

    public SendMessagesHandler(IMenuSender menuSender) {
        this.menuSender = menuSender;
    }

    @Override
    public void handle(Message message) {
        menuSender.send(message.getChatId(), PropertiesMessage.SEND_MESSAGES_MENU, Menu.SEND_MESSAGES);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.SEND_MESSAGES;
    }
}
