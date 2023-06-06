package tgb.btc.lib.service.impl;

import org.apache.commons.lang.StringUtils;
import tgb.btc.lib.enums.BotProperties;
import tgb.btc.lib.exception.PropertyValueNotFoundException;
import tgb.btc.lib.service.IValidateService;

public class CurrenciesTurningValidateService implements IValidateService {

    @Override
    public void validate(BotProperties botProperties)  throws PropertyValueNotFoundException {
        for (String key : botProperties.getKeys()) {
            String value = botProperties.getString(key);
            if (StringUtils.isBlank(value)) {
                throw new PropertyValueNotFoundException("Не указано значение для ключа " + key + ".");
            }
            if (!StringUtils.equals("true",value) && !StringUtils.equals("false",value)) {
                throw new PropertyValueNotFoundException("Не корректное значение для ключа " + key + ".");
            }
        }
    }
}
