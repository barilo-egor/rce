package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.exception.BaseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

@Slf4j
public final class BotPropertiesUtil {
    private BotPropertiesUtil() {
    }

    private static final String BOT_PROPERTIES_FILE_PATH = "config/bot/bot.properties";

    private static Properties botProperties;

    private static String BOT_TOKEN;

    private static String BOT_USERNAME;

    public static void loadProperties() {
        botProperties = new Properties();
        try {
            botProperties.load(new FileInputStream(BOT_PROPERTIES_FILE_PATH));
        } catch (IOException e) {
            log.error("Ошибка загрузки bot properties: ", e);
        }
    }

    public static String getToken() {
        if (Objects.isNull(BOT_TOKEN)) {
            BOT_TOKEN = botProperties.getProperty("bot.token");
            if (Objects.isNull(BOT_TOKEN)) throw new BaseException("Токен бота не найден.");
        }
        return BOT_TOKEN;
    }

    public static String getUsername() {
        if (Objects.isNull(BOT_USERNAME)) {
            BOT_USERNAME = botProperties.getProperty("bot.username");
            if (Objects.isNull(BOT_TOKEN)) throw new BaseException("Юзернейм бота не найден.");
        }
        return BOT_USERNAME;
    }
}
