package tgb.btc.rce.service.handler.impl.message.text.slash;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.service.enums.IChangeRoleService;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;

@Service
public class MakeUserHandler implements ISlashCommandHandler {

    private final IChangeRoleService changeRoleService;

    public MakeUserHandler(IChangeRoleService changeRoleService) {
        this.changeRoleService = changeRoleService;
    }

    @Override
    public void handle(Message message) {
        changeRoleService.changeRole(message, UserRole.USER);
    }

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.MAKE_USER;
    }
}
