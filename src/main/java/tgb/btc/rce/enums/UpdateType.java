package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.exception.BaseException;

public enum UpdateType {
    MESSAGE,
    CALLBACK_QUERY,
    INLINE_QUERY,
    CHANNEL_POST;

    public static UpdateType fromUpdate(Update update) {
        if (update.hasMessage()) return MESSAGE;
        if (update.hasCallbackQuery()) return CALLBACK_QUERY;
        if (update.hasInlineQuery()) return INLINE_QUERY;
        if (update.hasChannelPost()) return CHANNEL_POST;
        throw new BaseException("Тип апдейта не найден: " + update);
    }
}
