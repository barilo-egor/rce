package tgb.btc.lib.util;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.exception.BaseException;

public final class CallbackQueryUtil {
    private CallbackQueryUtil() {
    }

    public static Integer messageId(Update update) {
        if (!update.hasCallbackQuery()) throw new BaseException("В update отсутствует CallbackQuery.");
        return update.getCallbackQuery().getMessage().getMessageId();
    }

    public static String getSplitData(Update update, int index) {
        return update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[index];
    }

    public static String getSplitData(CallbackQuery callbackQuery, int index) {
        return callbackQuery.getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[index];
    }

    public static Long getSplitLongData(Update update, int index) {
        return Long.parseLong(getSplitData(update, index));
    }

    public static String buildCallbackData(String... variables) {
        return String.join(BotStringConstants.CALLBACK_DATA_SPLITTER, variables);
    }

    public static String buildCallbackData(Command command, String... variables) {
        return command.getText().concat(BotStringConstants.CALLBACK_DATA_SPLITTER).concat(String.join(BotStringConstants.CALLBACK_DATA_SPLITTER, variables));
    }

    public static boolean isBack(Update update) {
        return update.hasCallbackQuery() && Command.BACK.getText().equals(update.getCallbackQuery().getData());
    }

}
