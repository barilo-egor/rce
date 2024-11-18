package tgb.btc.rce.service.enums;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.UserRole;

public interface IChangeRoleService {

    void changeRole(Message message, UserRole role);
}
