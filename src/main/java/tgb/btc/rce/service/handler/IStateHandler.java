package tgb.btc.rce.service.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UserState;

public interface IStateHandler {

    void handle(Update update);

    UserState getUserState();
}
