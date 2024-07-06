package tgb.btc.rce.service.impl.processors.role;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;

@CommandProcessor(command = Command.MAKE_USER)
@Slf4j
public class MakeUser extends ChangeRoleProcessor {
    @Override
    public void run(Update update) {
        changeRole(update, UserRole.USER);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
