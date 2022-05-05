package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.exception.BaseException;

public class UpdateUtil {

    public static Long getChatId(Update update) {
        return getFrom(update).getId();
    }

    public static String getUsername(Update update) {
        return getFrom(update).getUserName();
    }

    private static User getFrom(Update update) {
        switch (UpdateType.fromUpdate(update)) {
            case MESSAGE:
                return update.getMessage().getFrom();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getFrom();
            default:
                throw new BaseException("Тип апдейта не найден: " + update);
        }
    }
}
