package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.util.IMenuService;

@Service
public class PaymentTypesHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IMenuService menuService;

    private final IReadUserService readUserService;

    public PaymentTypesHandler(IResponseSender responseSender, IMenuService menuService, IReadUserService readUserService) {
        this.responseSender = responseSender;
        this.menuService = menuService;
        this.readUserService = readUserService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        responseSender.sendMessage(chatId,
                "Меню управления типами оплаты.",
                menuService.build(Menu.PAYMENT_TYPES, readUserService.getUserRoleByChatId(chatId)));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.PAYMENT_TYPES;
    }
}
