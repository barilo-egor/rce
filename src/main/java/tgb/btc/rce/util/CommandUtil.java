package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.exception.BaseException;

public final class CommandUtil {
    private CommandUtil() {
    }

    public static boolean isStartCommand(Update update) {
        Command command;
        if (UpdateType.CALLBACK_QUERY.equals(UpdateType.fromUpdate(update)) || UpdateType.MESSAGE.equals(UpdateType.fromUpdate(update)))
            command = Command.fromUpdate(update);
        else return false;
        return UpdateType.MESSAGE.equals(UpdateType.fromUpdate(update))
                && Command.START.equals(command);
    }
}
