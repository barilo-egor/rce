package tgb.btc.rce.util;

import tgb.btc.rce.exception.BaseException;

public final class NumberUtil {
    private NumberUtil() {
    }

    public static Integer getInputInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new BaseException("Неверный формат.");
        }
    }

    public static Long getInputLong(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new BaseException("Неверный формат.");
        }
    }
}
