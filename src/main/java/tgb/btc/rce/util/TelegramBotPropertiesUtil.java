package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.enums.properties.BotProperties;

import java.util.Objects;

@Slf4j
public final class TelegramBotPropertiesUtil {
    private TelegramBotPropertiesUtil() {
    }

    private static String BOT_TOKEN;

    private static String BOT_USERNAME;

    public static String getToken() {
        if (Objects.isNull(BOT_TOKEN)) {
            BOT_TOKEN = BotProperties.BOT_CONFIG.getString("bot.token");
            if (Objects.isNull(BOT_TOKEN)) throw new BaseException("Токен бота не найден.");
        }
        return BOT_TOKEN;
    }

    public static String getUsername() {
        if (Objects.isNull(BOT_USERNAME)) {
            BOT_USERNAME = BotProperties.BOT_CONFIG.getString("bot.username");
            if (Objects.isNull(BOT_TOKEN)) throw new BaseException("Юзернейм бота не найден.");
        }
        return BOT_USERNAME;
    }

}
