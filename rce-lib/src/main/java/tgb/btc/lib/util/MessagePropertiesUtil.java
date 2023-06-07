package tgb.btc.lib.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.lib.enums.BotProperties;
import tgb.btc.lib.enums.DealType;
import tgb.btc.lib.enums.PropertiesMessage;

import java.util.Objects;

@Slf4j
public class MessagePropertiesUtil {

    private final static String errorMessage = "Не найдено сообщение по ключу %s";

    public static String getMessage(PropertiesMessage message) {
        String text;
        try {
            text = BotProperties.MESSAGE_PROPERTIES.getString(message.getKey()).replaceAll("<ln>", "\n");
        } catch (Exception e) {
            text = getErrorText(message.getKey());
        }
        if (Objects.isNull(text)) text = getErrorText(message.getKey());
        return text;
    }

    public static String getMessage(PropertiesMessage message, Object... variables) {
        return String.format(getMessage(message), variables);
    }

    public static String getMessage(String key) {
        String text;
        try {
            text = BotProperties.MESSAGE_PROPERTIES.getString(key).replaceAll("<ln>", "\n");
        } catch (Exception e) {
            text = getErrorText(key);
        }
        if (Objects.isNull(text)) text = getErrorText(key);
        return text;
    }

    private static String getErrorText(String key) {
        return String.format(errorMessage, key);
    }

    public static String getChooseCurrency(DealType dealType) {
        return getMessage(DealType.BUY.equals(dealType)
                ? PropertiesMessage.CHOOSE_CURRENCY_BUY
                : PropertiesMessage.CHOOSE_CURRENCY_SELL);
    }
}
