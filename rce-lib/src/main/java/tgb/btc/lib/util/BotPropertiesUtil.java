package tgb.btc.lib.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.lib.enums.BotProperties;
import tgb.btc.lib.exception.BaseException;

import java.util.Objects;

@Slf4j
public final class BotPropertiesUtil {
    private BotPropertiesUtil() {
    }

    private static String BOT_TOKEN;

    private static String BOT_USERNAME;

    public static String getToken() {
        if (Objects.isNull(BOT_TOKEN)) {
            BOT_TOKEN = BotProperties.BOT_CONFIG_PROPERTIES.getString("bot.token");
            if (Objects.isNull(BOT_TOKEN)) throw new BaseException("Токен бота не найден.");
        }
        return BOT_TOKEN;
    }

    public static String getUsername() {
        if (Objects.isNull(BOT_USERNAME)) {
            BOT_USERNAME = BotProperties.BOT_CONFIG_PROPERTIES.getString("bot.username");
            if (Objects.isNull(BOT_TOKEN)) throw new BaseException("Юзернейм бота не найден.");
        }
        return BOT_USERNAME;
    }

}
