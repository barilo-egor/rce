package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PropertiesMessage;

import java.util.Objects;

@Slf4j
public class MessagePropertiesUtil {

    private final static String errorMessage = "Не найдено сообщение по ключу %s";

    public static String getMessage(PropertiesMessage message) {
        String text;
        try {
            String propertiesMessage = BotProperties.MESSAGE.getString(message.getKey());
            if (Objects.isNull(propertiesMessage)) return null;
            text = propertiesMessage.replaceAll("<ln>", "\n");
        } catch (Exception e) {
            text = getErrorText(message.getKey());
        }
        if (Objects.isNull(text)) text = getErrorText(message.getKey());
        return text;
    }

    public static String getMessage(PropertiesMessage message, Object... variables) {
        String propertiesMessage = getMessage(message);
        if (Objects.isNull(propertiesMessage)) return null; // TODO переделать на наллсейф,и не налл сейф
        return String.format(propertiesMessage, variables);
    }

    public static String getMessage(String key, Object... variables) {
        String propertiesMessage = getMessageForFormat(key);
        if (Objects.isNull(propertiesMessage)) return null; // TODO переделать на наллсейф,и не налл сейф
        return String.format(propertiesMessage, variables);
    }

    public static String getMessage(String key) {
        String text;
        try {
            String message = BotProperties.MESSAGE.getString(key);
            if (Objects.isNull(message)) return null;
            text = message.replaceAll("<ln>", "\n");
        } catch (Exception e) {
            text = getErrorText(key);
        }
        if (Objects.isNull(text)) text = getErrorText(key);
        return text;
    }


    public static String getMessageForFormat(String key) {
        String text;
        try {
            String message = BotProperties.MESSAGE.getString(key);
            if (Objects.isNull(message)) return null;
            text = message.replaceAll("<ln>", "%n");
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
