package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
public class BotVariablePropertiesUtil {

    private final static String wrongFormat = "Неверный формат переменной по ключу %s";

    public static String getVariable(BotVariableType botVariableType) {
        String text;
        try {
            text = BotProperties.BOT_VARIABLE.getString(botVariableType.getKey());
        } catch (Exception e) {
            throw new BaseException("Переменная по ключу " + botVariableType.getKey() + " не найдена.");
        }
        if (Objects.isNull(text))
            throw new BaseException("Переменная по ключу " + botVariableType.getKey() + " не найдена.");
        return text;
    }

    public static String getVariable(BotVariableType botVariableType, FiatCurrency fiatCurrency,
                                     DealType dealType, CryptoCurrency cryptoCurrency) {
        String text;
        String key = botVariableType.getKey() + "."
                + fiatCurrency.getCode() + "."
                + dealType.getKey() + "."
                + cryptoCurrency.getShortName();
        try {
            text = BotProperties.BOT_VARIABLE.getString(botVariableType.getKey() + "."
                    + fiatCurrency.getCode() + "."
                    + dealType.getKey() + "."
                    + cryptoCurrency.getShortName());
        } catch (Exception e) {
            throw new BaseException("Переменная по ключу " + key + " не найдена.");
        }
        if (Objects.isNull(text))
            throw new BaseException("Переменная по ключу " + botVariableType.getKey() + " не найдена.");
        return text;
    }

    public static String getVariable(BotVariableType botVariableType, DealType dealType, CryptoCurrency cryptoCurrency) {
        String text;
        String key = botVariableType.getKey() + "."
                + dealType.getKey() + "."
                + cryptoCurrency.getShortName();
        try {
            text = BotProperties.BOT_VARIABLE.getString(key);
        } catch (Exception e) {
            throw new BaseException("Переменная по ключу " + key + " не найдена.");
        }
        if (Objects.isNull(text))
            throw new BaseException("Переменная по ключу " + key + " не найдена.");
        return text;
    }

    public static BigDecimal getBigDecimal(BotVariableType botVariableType, FiatCurrency fiatCurrency, DealType dealType,
                                           CryptoCurrency cryptoCurrency) {
        return BigDecimal.valueOf(Double.parseDouble(getVariable(botVariableType, fiatCurrency, dealType, cryptoCurrency)));
    }

    public static BigDecimal getBigDecimal(BotVariableType botVariableType, DealType dealType, CryptoCurrency cryptoCurrency) {
        return BigDecimal.valueOf(getDouble(botVariableType, dealType, cryptoCurrency));
    }

    public static Double getDouble(BotVariableType botVariableType, DealType dealType, CryptoCurrency cryptoCurrency) {
        return Double.parseDouble(getVariable(botVariableType, dealType, cryptoCurrency));
    }


    public static Float getFloat(BotVariableType botVariableType) {
        try {
            return Float.parseFloat(getVariable(botVariableType));
        } catch (NumberFormatException e) {
            throw new BaseException(String.format(wrongFormat, botVariableType.getKey()));
        }
    }

    public static Double getDouble(BotVariableType botVariableType) {
        try {
            return Double.parseDouble(getVariable(botVariableType));
        } catch (NumberFormatException e) {
            throw new BaseException(String.format(wrongFormat, botVariableType.getKey()));
        }
    }

    public static Boolean getBoolean(BotVariableType botVariableType) {
        return Boolean.parseBoolean(getVariable(botVariableType));
    }

    public static Integer getInt(BotVariableType botVariableType) {
        try {
            return Integer.parseInt(getVariable(botVariableType));
        } catch (NumberFormatException e) {
            throw new BaseException(String.format(wrongFormat, botVariableType.getKey()));
        }
    }

    public static BigDecimal getTransactionCommission(CryptoCurrency cryptoCurrency) {
        return getBigDecimal(BotVariableType.TRANSACTION_COMMISSION.getKey(cryptoCurrency));
    }

    public static BigDecimal getBigDecimal(String key) {
        return BotProperties.BOT_VARIABLE.getBigDecimal(key);
    }

    public static Double getDouble(String key) {
        return BotProperties.BOT_VARIABLE.getDouble(key);
    }

    public static String getWallet(CryptoCurrency cryptoCurrency) {
        return BotProperties.BOT_VARIABLE.getString(BotVariableType.WALLET.getKey(cryptoCurrency));
    }
}
