package tgb.btc.rce.service.handler.message.text;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.UserRole;

import java.util.Set;

public interface ISimpleTextHandler {

    void handle(Message message);

    String getText();

    Set<UserRole> getRoles();
}
