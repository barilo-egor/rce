package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.constants.BotStringConstants;

public final class CallbackQueryUtil {
    private CallbackQueryUtil() {
    }

    public static String getSplitData(Update update, int index) {
        return update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[index];
    }

    public static Long getSplitLongData(Update update, int index) {
        return Long.parseLong(getSplitData(update, index));
    }
}