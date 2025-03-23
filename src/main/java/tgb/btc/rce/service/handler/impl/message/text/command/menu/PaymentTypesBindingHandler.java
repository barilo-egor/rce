package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class PaymentTypesBindingHandler implements ITextCommandHandler {

    private final IMenuSender menuSender;

    public PaymentTypesBindingHandler(IMenuSender menuSender) {
        this.menuSender = menuSender;
    }

    @Override
    public void handle(Message message) {
        menuSender.send(message.getChatId(), "Меню привязки типов оплат к мерчантам.", Menu.PAYMENT_TYPES_BINDING);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.PAYMENT_TYPES_BINDING;
    }
}
