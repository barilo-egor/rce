package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.UpdateType;

import java.math.BigDecimal;
import java.util.Objects;

public class UpdateUtil {

    public static Long getChatId(Update update) {
        return getFrom(update).getId();
    }

    public static Long getGroupChatId(Update update) {
        if (update.hasMyChatMember())
            return update.getMyChatMember().getChat().getId();
        if (update.hasChatMember())
            return update.getChatMember().getChat().getId();
        if (update.hasMessage())
            return update.getMessage().getChat().getId();
        else return null;
    }

    public static boolean isGroupMessage(Update update) {
        return (update.hasMyChatMember() &&
                (update.getMyChatMember().getChat().getType().equals("group") || update.getMyChatMember().getChat().getType().equals("supergroup")))
                || (update.hasMessage() && (update.getMessage().getChat().isGroupChat()
                || update.getMessage().isGroupMessage()
                || update.getMessage().isSuperGroupMessage()));
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
            case INLINE_QUERY:
                return update.getInlineQuery().getFrom();
            default:
                throw new BaseException("Тип апдейта не найден: " + update);
        }
    }

    public static Integer getMessageId(Update update) {
        if (Objects.isNull(update.getMessage()))
            throw new BaseException("Невозможно получить message id, т.к. message==null.");
        return update.getMessage().getMessageId();
    }

    public static boolean hasMessageText(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    public static String getMessageText(Update update) {
        if (Objects.isNull(update.getMessage()))
            throw new BaseException("Невозможно получить message id, т.к. message==null.");
        return update.getMessage().getText();
    }

    public static Long getLongFromText(Update update) {
        return NumberUtil.getInputLong(getMessageText(update));
    }

    public static Integer getIntFromText(Update update) {
        return NumberUtil.getInputInt(getMessageText(update));
    }

    public static Double getDoubleFromText(Update update) {
        return NumberUtil.getInputDouble(getMessageText(update).replaceAll(",", "."));
    }

    public static BigDecimal getBigDecimalFromText(Update update) {
        return BigDecimal.valueOf(NumberUtil.getInputDouble(getMessageText(update).replaceAll(",", ".")));
    }

    public static Message getMessage(Update update) {
        switch (UpdateType.fromUpdate(update)) {
            case MESSAGE:
                return update.getMessage();
            case CALLBACK_QUERY:
                return update.getCallbackQuery().getMessage();
            default:
                throw new BaseException("Не найден тип апдейта для получения месседжа.");
        }
    }
}
