package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.util.IStartService;

@Service
public class QuitAdminPanelHandler implements ITextCommandHandler {

    private final IStartService startService;

    public QuitAdminPanelHandler(IStartService startService) {
        this.startService = startService;
    }

    @Override
    public void handle(Message message) {
        startService.processToMainMenu(message.getChatId());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.QUIT_ADMIN_PANEL;
    }
}
