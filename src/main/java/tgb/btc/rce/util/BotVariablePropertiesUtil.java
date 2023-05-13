package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.exception.BaseException;

import java.io.File;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Properties;

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
    public static void validate(File file) throws BaseException {
        // TODO
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
        BotVariableType type = DealType.isBuy(dealType) ? BotVariableType.FIX_COMMISSION_BUY
                                                        : BotVariableType.FIX_COMMISSION_SELL;
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
