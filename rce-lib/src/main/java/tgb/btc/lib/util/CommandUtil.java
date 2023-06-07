package tgb.btc.lib.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.enums.Command;

public final class CommandUtil {
    private CommandUtil() {
    }

    public static boolean isStartCommand(Update update) {
        return UpdateUtil.hasMessageText(update) && Command.START.equals(Command.fromUpdate(update));
    }
}
