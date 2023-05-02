package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.exception.BaseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class MessagePropertiesUtil {

    private final static String errorMessage = "Не найдено сообщение по ключу %s";
    private final static Properties messageProperties = new Properties();

    public static void loadProperties() {
        try (FileInputStream inputStream = new FileInputStream(FilePaths.MESSAGE_PROPERTIES)){
            messageProperties.load(inputStream);
        } catch (IOException e) {
            log.error("Ошибка загрузки bot properties по пути " + FilePaths.MESSAGE_PROPERTIES + " : ", e);
        }
    }

    public static String getMessage(PropertiesMessage message) {
        String text;
        try {
            text = messageProperties.getProperty(message.getKey()).replaceAll("<ln>", "\n");
        } catch (Exception e) {
            text = getErrorText(message.getKey());
        }
        if (Objects.isNull(text)) text = getErrorText(message.getKey());
        return text;
    }

    public static String getMessage(PropertiesMessage message, Object... variables) {
        return String.format(getMessage(message), variables);
    }

    public static String getMessage(String key) {
        String text;
        try {
            text = messageProperties.getProperty(key).replaceAll("<ln>", "\n");
        } catch (Exception e) {
            text = getErrorText(key);
        }
        if (Objects.isNull(text)) text = getErrorText(key);
        return text;
    }

    private static String getErrorText(String key) {
        return String.format(errorMessage, key);
    }

    public static void validate(File file) throws BaseException {

    }

    public static String getChooseCurrency(DealType dealType) {
        return getMessage(DealType.BUY.equals(dealType)
                ? PropertiesMessage.CHOOSE_CURRENCY_BUY
                : PropertiesMessage.CHOOSE_CURRENCY_SELL);
    }
}
