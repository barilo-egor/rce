package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class OperatorPanelHandler implements ITextCommandHandler {

    public static final String MESSAGE = "Вы перешли в панель оператора.";

    private final IMenuSender menuSender;

    public OperatorPanelHandler(IMenuSender menuSender) {
        this.menuSender = menuSender;
    }

    @Override
    public void handle(Message message) {
        menuSender.send(message.getChatId(), MESSAGE, Menu.OPERATOR_PANEL);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.OPERATOR_PANEL;
    }
}
