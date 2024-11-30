package tgb.btc.rce.service.handler.impl.message.text.command.util;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;

@Service
public class ReturnHandler implements ITextCommandHandler {

    private final IAdminPanelService adminPanelService;

    public ReturnHandler(IAdminPanelService adminPanelService) {
        this.adminPanelService = adminPanelService;
    }

    @Override
    public void handle(Message message) {
        adminPanelService.send(message.getChatId());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.RETURN;
    }
}
