package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.UpdateType;

public final class CommandUtil {
    private CommandUtil() {
    }

    public static boolean isStartCommand(Update update) {
        return UpdateType.MESSAGE.equals(UpdateType.fromUpdate(update))
                && Command.START.equals(Command.fromUpdate(update));
    }
}
