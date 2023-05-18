package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.PropertyValueNotFoundException;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
public class BotVariablePropertiesUtil {

    private final static String wrongFormat = "Неверный формат переменной по ключу %s";

    public static String getVariable(BotVariableType botVariableType) {
        String text;
        try {
            text = BotProperties.BOT_VARIABLE_PROPERTIES.getString(botVariableType.getKey());
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
            text = BotProperties.BOT_VARIABLE_PROPERTIES.getString(botVariableType.getKey() + "."
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
            text = BotProperties.BOT_VARIABLE_PROPERTIES.getString(key);
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

    public static void validate(BotProperties botProperties) throws PropertyValueNotFoundException {
        for (String key : botProperties.getKeys()) {
            if (Objects.isNull(botProperties.getString(key))) {
                throw new PropertyValueNotFoundException("Не корректно указано значение для переменной " + key + ".");
            }
        }
    }

    public static Double getMinSum(CryptoCurrency cryptoCurrency, DealType dealType) {
        BotVariableType botVariableType = DealType.isBuy(dealType) ? BotVariableType.MIN_SUM_BUY : BotVariableType.MIN_SUM_SELL;
        return BotProperties.BOT_VARIABLE_PROPERTIES.getDouble(botVariableType.getKey(cryptoCurrency));
    }

    public static BigDecimal getFix(CryptoCurrency cryptoCurrency, DealType dealType) {
        BotVariableType type = DealType.isBuy(dealType) ? BotVariableType.FIX_BUY : BotVariableType.FIX_SELL;
        return getBigDecimal(type.getKey(cryptoCurrency));
    }

    public static BigDecimal getFixCommission(CryptoCurrency cryptoCurrency, DealType dealType) {
        BotVariableType type = DealType.isBuy(dealType) ? BotVariableType.FIX_COMMISSION_BUY
                                                        : BotVariableType.FIX_COMMISSION_SELL;
        return getBigDecimal(type.getKey(cryptoCurrency));
    }

    public static BigDecimal getCommission(CryptoCurrency cryptoCurrency, DealType dealType) {
        BotVariableType type = DealType.isBuy(dealType) ? BotVariableType.COMMISSION_BUY
                                                        : BotVariableType.COMMISSION_SELL;
        return getBigDecimal(type.getKey(cryptoCurrency));
    }

    public static BigDecimal getTransactionCommission(CryptoCurrency cryptoCurrency) {
        return getBigDecimal(BotVariableType.TRANSACTION_COMMISSION.getKey(cryptoCurrency));
    }

    private static BigDecimal getBigDecimal(String key) {
        return BotProperties.BOT_VARIABLE_PROPERTIES.getBigDecimal(key);
    }

    public static String getWallet(CryptoCurrency cryptoCurrency) {
        return BotProperties.BOT_VARIABLE_PROPERTIES.getString(BotVariableType.WALLET.getKey(cryptoCurrency));
    }
}
