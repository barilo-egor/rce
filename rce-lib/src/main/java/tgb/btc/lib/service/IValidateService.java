package tgb.btc.lib.service;


import tgb.btc.lib.enums.BotProperties;
import tgb.btc.lib.exception.PropertyValueNotFoundException;

import java.util.Objects;

public interface IValidateService {

    default void validate(BotProperties botProperties) throws PropertyValueNotFoundException {
        for (String key : botProperties.getKeys()) {
            String value = botProperties.getString(key);
            if (Objects.isNull(value)) {
                throw new PropertyValueNotFoundException("Не корректно указано значение для " + key + ".");
            }
        }
    }

    default void load() {
    }
}
