package tgb.btc.rce.service.handler.message.text;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.UserState;

public interface IStateTextMessageHandler {

    void handle(Message message);

    UserState getUserState();
}
