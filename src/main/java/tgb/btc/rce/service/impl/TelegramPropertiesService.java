package tgb.btc.rce.service.impl;

import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.service.ITelegramPropertiesService;

import java.util.Objects;

@Service
public class TelegramPropertiesService implements ITelegramPropertiesService {

    private String botToken;

    private String botUsername;

    @Override
    public String getToken() {
        if (Objects.isNull(botToken)) {
            botToken = PropertiesPath.BOT_PROPERTIES.getString("bot.token");
            if (Objects.isNull(botToken)) throw new BaseException("Токен бота не найден.");
        }
        return botToken;
    }

    @Override
    public String getUsername() {
        if (Objects.isNull(botUsername)) {
            botUsername = PropertiesPath.BOT_PROPERTIES.getString("bot.username");
            if (Objects.isNull(botToken)) throw new BaseException("Юзернейм бота не найден.");
        }
        return botUsername;
    }
}
