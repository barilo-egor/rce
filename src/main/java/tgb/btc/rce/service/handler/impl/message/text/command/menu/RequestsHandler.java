package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class RequestsHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    public RequestsHandler(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        responseSender.sendMessage(message.getChatId(), "Меню заявок", Menu.REQUESTS);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.REQUESTS;
    }
}
