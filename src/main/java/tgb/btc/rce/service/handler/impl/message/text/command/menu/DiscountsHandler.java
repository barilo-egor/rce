package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IMenuSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class DiscountsHandler implements ITextCommandHandler {

    private final IMenuSender menuSender;

    public DiscountsHandler(IMenuSender menuSender) {
        this.menuSender = menuSender;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        menuSender.send(chatId,"Меню управления скидками.", Menu.DISCOUNTS);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.DISCOUNTS;
    }
}
