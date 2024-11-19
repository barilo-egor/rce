package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.service.properties.BotPropertiesReader;
import tgb.btc.rce.service.util.ITelegramPropertiesService;

import java.util.Objects;

@Service
public class TelegramPropertiesService implements ITelegramPropertiesService {

    private String botToken;

    private String botUsername;

    private BotPropertiesReader botPropertiesReader;

    @Autowired
    public void setBotPropertiesReader(BotPropertiesReader botPropertiesReader) {
        this.botPropertiesReader = botPropertiesReader;
    }

    @Override
    @Cacheable("botTokenCache")
    public String getToken() {
        if (Objects.isNull(botToken)) {
            botToken = botPropertiesReader.getString("bot.token");
            if (Objects.isNull(botToken)) throw new BaseException("Токен бота не найден.");
        }
        return botToken;
    }

    @Override
    @Cacheable("botUsernameCache")
    public String getUsername() {
        if (Objects.isNull(botUsername)) {
            botUsername = botPropertiesReader.getString("bot.username");
            if (Objects.isNull(botToken)) throw new BaseException("Юзернейм бота не найден.");
        }
        return botUsername;
    }
}
