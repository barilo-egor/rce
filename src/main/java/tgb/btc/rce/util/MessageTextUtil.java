package tgb.btc.rce.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.exception.BaseException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Objects;

public final class MessageTextUtil {
    private MessageTextUtil() {
    }

    public static LocalDate getDate(Update update) {
        if (!UpdateUtil.hasMessageText(update)) throw new BaseException("Отсутствует message text.");
        String[] values = UpdateUtil.getMessageText(update).split("\\.");
        try {
            if (values.length != 3) throw new BaseException("Неверный формат даты.");
            return LocalDate.of(Integer.parseInt(values[2]), Integer.parseInt(values[1]), Integer.parseInt(values[0]));
        } catch (DateTimeException e) {
            throw new BaseException("Неверный формат даты.");
        }
    }
}
