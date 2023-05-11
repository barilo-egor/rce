package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.constants.FilePaths;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class AntiSpamPropertiesUtil {

    private final static Properties ANTI_SPAM_PROPERTIES = new Properties();

    public static void loadProperties() {
        try {
            ANTI_SPAM_PROPERTIES.load(new FileInputStream(FilePaths.ANTI_SPAM_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибка загрузки antispam.properties по пути " + FilePaths.ANTI_SPAM_PROPERTIES + " : ", e);
        }
    }

    public static String getProperty(String key) {
        return ANTI_SPAM_PROPERTIES.getProperty(key);
    }

    public static Integer getInt(String key) {
        return Integer.parseInt(getProperty(key));
    }
}
