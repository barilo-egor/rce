package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.exception.BaseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class BotVariablePropertiesUtil {

    private final static String wrongFormat = "Неверный формат переменной по ключу %s";

    private final static Properties botVariableProperties = new Properties();

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

    public static Double getMinSumBuy(CryptoCurrency cryptoCurrency) {
        switch (cryptoCurrency) {
            case BITCOIN:
                return BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM_BUY_BTC);
            case LITECOIN:
                return BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM_BUY_LTC);
            case USDT:
                return BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM_BUY_USDT);
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
    }

    public static Double getMinSumSell(CryptoCurrency cryptoCurrency) {
        switch (cryptoCurrency) {
            case BITCOIN:
                return BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM_SELL_BTC);
            case LITECOIN:
                return BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM_SELL_LTC);
            case USDT:
                return BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM_SELL_USDT);
            default:
                throw new BaseException("Не определена крипто валюта.");
        }
    }

    public static void validate(File file) throws BaseException {
        // TODO
    }
}
