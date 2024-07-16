package tgb.btc.rce.service.util;

import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.rce.enums.PropertiesMessage;

public interface IMessagePropertiesService {
    String getMessage(PropertiesMessage message);

    String getMessage(PropertiesMessage message, Object... variables);

    String getMessage(String key, Object... variables);

    String getMessage(String key);

    String getMessageForFormat(String key);

    String getChooseCurrency(DealType dealType);
}
