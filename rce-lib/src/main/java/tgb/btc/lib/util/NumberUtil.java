package tgb.btc.lib.util;

import tgb.btc.lib.exception.NumberParseException;

public final class NumberUtil {
    private NumberUtil() {
    }

    public static Integer getInputInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new NumberParseException("Неверный формат.");
        }
    }

    public static Long getInputLong(String input) {
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            throw new NumberParseException("Неверный формат.");
        }
    }

    public static Double getInputDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new NumberParseException("Неверный формат.");
        }
    }
}
