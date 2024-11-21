package tgb.btc.rce.service.handler.message.contact;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;

public interface IStateContactMessageHandler {

    void handle(Message message);

    UserState getUserState();
}
