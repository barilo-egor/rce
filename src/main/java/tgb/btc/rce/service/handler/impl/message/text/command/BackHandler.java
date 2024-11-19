package tgb.btc.rce.service.handler.impl.message.text.command;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.interfaces.service.bean.bot.IBotMessageService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.util.IMenuService;

@Service
public class BackHandler implements ITextCommandHandler {

    public final IResponseSender responseSender;

    private final IBotMessageService botMessageService;

    private final IMenuService menuService;

    private final IReadUserService readUserService;

    public BackHandler(IResponseSender responseSender, IBotMessageService botMessageService, IMenuService menuService,
                       IReadUserService readUserService) {
        this.responseSender = responseSender;
        this.botMessageService = botMessageService;
        this.menuService = menuService;
        this.readUserService = readUserService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        responseSender.sendBotMessage(botMessageService.findByTypeNullSafe(BotMessageType.START),
                chatId,
                menuService.build(Menu.MAIN, readUserService.getUserRoleByChatId(chatId)));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BACK;
    }
}
