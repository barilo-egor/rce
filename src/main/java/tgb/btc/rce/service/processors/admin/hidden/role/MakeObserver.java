package tgb.btc.rce.service.processors.admin.hidden.role;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;

@Slf4j
@CommandProcessor(command = Command.MAKE_OBSERVER)
public class MakeObserver extends ChangeRoleProcessor {
    @Override
    public void run(Update update) {
        changeRole(update, UserRole.OBSERVER);
    }

    @Override
    protected Logger getLogger() {
        return log;
    }
}
