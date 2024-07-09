package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static Boolean getSplitBooleanData(Update update, int index) {
        return Boolean.parseBoolean(getSplitData(update, index));
    }


    public static String buildCallbackData(String... variables) {
        return String.join(BotStringConstants.CALLBACK_DATA_SPLITTER, variables);
    }

    public static String buildCallbackData(Command command, String... variables) {
        return command.getText().concat(BotStringConstants.CALLBACK_DATA_SPLITTER).concat(String.join(BotStringConstants.CALLBACK_DATA_SPLITTER, variables));
    }

    public static String buildCallbackData(Command command, Object... variables) {
        return command.getText().concat(BotStringConstants.CALLBACK_DATA_SPLITTER)
                .concat(Arrays.stream(variables).map(Object::toString)
                        .collect(Collectors.joining(BotStringConstants.CALLBACK_DATA_SPLITTER)));
    }

    public static boolean isBack(Update update) {
        return update.hasCallbackQuery() && Command.BACK.getText().equals(update.getCallbackQuery().getData());
    }

}
