package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;

public final class CommandUtil {
    private CommandUtil() {
    }

    public static boolean isStartCommand(Update update) {
        return UpdateUtil.hasMessageText(update) && Command.START.equals(Command.fromUpdate(update));
    }

    public static boolean isSubmitCommand(Update update) {
        return update.hasCallbackQuery() &&
                (update.getCallbackQuery().getData().startsWith(Command.SUBMIT_LOGIN.name())
                        || update.getCallbackQuery().getData().startsWith(Command.SUBMIT_REGISTER.name()));
    }
}
