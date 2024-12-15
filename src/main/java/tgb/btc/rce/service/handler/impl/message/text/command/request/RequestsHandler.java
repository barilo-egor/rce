package tgb.btc.rce.service.handler.impl.message.text.command.request;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class RequestsHandler implements ITextCommandHandler {

    private final IMenuSender menuSender;

    public RequestsHandler(IMenuSender menuSender) {
        this.menuSender = menuSender;
    }

    @Override
    public void handle(Message message) {
        menuSender.send(message.getChatId(), "Меню заявок", Menu.REQUESTS);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.REQUESTS;
    }
}
