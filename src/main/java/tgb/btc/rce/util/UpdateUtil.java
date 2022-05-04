package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.exception.BaseException;

public class UpdateUtil {

    public static Long getChatId(Update update) {
        switch (UpdateType.fromUpdate(update)) {
            case MESSAGE:
                return update.getMessage().getFrom().getId();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getFrom().getId();
            default:
                throw new BaseException("Тип апдейта не найден: " + update);
        }
    }
}
