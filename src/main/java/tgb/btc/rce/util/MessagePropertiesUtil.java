package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.PropertiesMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public class MessagePropertiesUtil {

    private final static String errorMessage = "Не найдено сообщение по ключу %s";
    private final static Properties messageProperties = new Properties();

    public static void loadProperties() {
        try {
            messageProperties.load(new FileInputStream(FilePaths.MESSAGE_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибка загрузки bot properties по пути " + FilePaths.MESSAGE_PROPERTIES + " : ", e);
        }
    }

    public static String getMessage(PropertiesMessage message) {
        String text;
        try {
            text = messageProperties.getProperty(message.getKey());
        } catch (Exception e) {
            text = getErrorText(message.getKey());
        }
        if (Objects.isNull(text)) text = getErrorText(message.getKey());
        return text;
    }

    private static String getErrorText(String key) {
        return String.format(errorMessage, key);
    }
}
