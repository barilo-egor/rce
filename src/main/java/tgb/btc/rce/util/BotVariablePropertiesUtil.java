package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.exception.BaseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class BotVariablePropertiesUtil {

    private final static String wrongFormat = "Неверный формат переменной по ключу %s";

    private final static Properties botVariableProperties = new Properties();

    public static void loadProperties() {
        try {
            botVariableProperties.load(new FileInputStream(FilePaths.BOT_VARIABLE_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибка загрузки bot variable properties по пути " + FilePaths.BOT_VARIABLE_PROPERTIES + " : ", e);
        }
    }

    public static String getVariable(BotVariableType botVariableType) {
        String text;
        try {
            text = botVariableProperties.getProperty(botVariableType.getKey());
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

    public static Integer getInt(BotVariableType botVariableType) {
        try {
            return Integer.parseInt(getVariable(botVariableType));
        } catch (NumberFormatException e) {
            throw new BaseException(String.format(wrongFormat, botVariableType.getKey()));
        }
    }

    public static Integer getMinSumForWithdrawal() {
        return getInt(BotVariableType.MIN_WITHDRAWAL_OF_FUNDS_SUM);
    }
}
