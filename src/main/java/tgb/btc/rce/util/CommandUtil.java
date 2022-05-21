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
        try {
            command = Command.fromUpdate(update);
        } catch (BaseException e) {
            return false;
        }
        return UpdateType.MESSAGE.equals(UpdateType.fromUpdate(update))
                && Command.START.equals(command);
    }
}
