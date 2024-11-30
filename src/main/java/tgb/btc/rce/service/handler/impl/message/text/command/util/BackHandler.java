package tgb.btc.rce.service.handler.impl.message.text.command.util;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.util.IStartService;

@Service
public class BackHandler implements ITextCommandHandler {

    private final IStartService startService;

    public BackHandler(IStartService startService) {
        this.startService = startService;
    }

    @Override
    public void handle(Message message) {
        startService.processToMainMenu(message.getChatId());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BACK;
    }
}
