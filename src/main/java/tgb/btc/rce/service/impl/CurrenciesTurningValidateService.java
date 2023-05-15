package tgb.btc.rce.service.impl;

import org.apache.commons.lang.StringUtils;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.service.IValidateService;

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
