package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class UsersHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    public UsersHandler(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        responseSender.sendMessage(message.getChatId(), "Меню для работы с пользователем", Menu.USERS);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.USERS;
    }
}
