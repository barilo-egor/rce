package tgb.btc.rce.service.handler.impl.message.text.command.menu;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;

@Service
public class BackHandler implements ITextCommandHandler {

    private final IAdminPanelService adminPanelService;

    public BackHandler(IAdminPanelService adminPanelService) {
        this.adminPanelService = adminPanelService;
    }

    @Override
    public void handle(Message message) {
        adminPanelService.send(message.getChatId());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.BACK;
    }
}
