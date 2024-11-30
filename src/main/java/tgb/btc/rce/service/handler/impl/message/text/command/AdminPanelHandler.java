package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class AdminPanelHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    public AdminPanelHandler(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        responseSender.sendMessage(message.getChatId(), PropertiesMessage.MENU_MAIN_ADMIN_MESSAGE, Menu.ADMIN_PANEL);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.ADMIN_PANEL;
    }
}
