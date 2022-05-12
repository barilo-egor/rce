package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.exception.BaseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public final class BotPropertiesUtil {
    private BotPropertiesUtil() {
    }



    private final static Properties botProperties = new Properties();

    private static String BOT_TOKEN;

    private static String BOT_USERNAME;

    public static void loadProperties() {
        try {
            botProperties.load(new FileInputStream(FilePaths.BOT_PROPERTIES));
        } catch (IOException e) {
            log.error("Ошибка загрузки bot properties по пути " + FilePaths.BOT_PROPERTIES + " : ", e);
        }
    }

    public static String getToken() {
        if (Objects.isNull(BOT_TOKEN)) {
            BOT_TOKEN = getProperty("bot.token");
            if (Objects.isNull(BOT_TOKEN)) throw new BaseException("Токен бота не найден.");
        }
        return BOT_TOKEN;
    }

    public static String getUsername() {
        if (Objects.isNull(BOT_USERNAME)) {
            BOT_USERNAME = getProperty("bot.username");
            if (Objects.isNull(BOT_TOKEN)) throw new BaseException("Юзернейм бота не найден.");
        }
        return BOT_USERNAME;
    }

    public static String getProperty(String key) {
        return botProperties.getProperty(key);
    }
}
