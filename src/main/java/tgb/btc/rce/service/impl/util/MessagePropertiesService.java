package tgb.btc.rce.service.impl.util;

import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.util.IMessagePropertiesService;

import java.util.Objects;

@Service
public class MessagePropertiesService implements IMessagePropertiesService {

    @Override
    public String getMessage(PropertiesMessage message) {
        String text;
        try {
            String propertiesMessage = PropertiesPath.MESSAGE_PROPERTIES.getString(message.getKey());
            if (Objects.isNull(propertiesMessage)) return null;
            text = propertiesMessage.replaceAll("<ln>", "\n");
        } catch (Exception e) {
            text = getErrorText(message.getKey());
        }
        if (Objects.isNull(text)) text = getErrorText(message.getKey());
        return text;
    }

    @Override
    public String getMessage(PropertiesMessage message, Object... variables) {
        String propertiesMessage = getMessage(message);
        if (Objects.isNull(propertiesMessage)) return null; // TODO переделать на наллсейф,и не налл сейф
        return String.format(propertiesMessage, variables);
    }

    @Override
    public String getMessage(String key, Object... variables) {
        String propertiesMessage = getMessageForFormat(key);
        if (Objects.isNull(propertiesMessage)) return null; // TODO переделать на наллсейф,и не налл сейф
        return String.format(propertiesMessage, variables);
    }

    @Override
    public String getMessage(String key) {
        String text;
        try {
            String message = PropertiesPath.MESSAGE_PROPERTIES.getString(key);
            if (Objects.isNull(message)) return null;
            text = message.replaceAll("<ln>", "\n");
        } catch (Exception e) {
            text = getErrorText(key);
        }
        if (Objects.isNull(text)) text = getErrorText(key);
        return text;
    }

    @Override
    public String getMessageForFormat(String key) {
        String text;
        try {
            String message = PropertiesPath.MESSAGE_PROPERTIES.getString(key);
            if (Objects.isNull(message)) return null;
            text = message.replaceAll("<ln>", "%n");
        } catch (Exception e) {
            text = getErrorText(key);
        }
        if (Objects.isNull(text)) text = getErrorText(key);
        return text;
    }

    private String getErrorText(String key) {
        String errorMessage = "Не найдено сообщение по ключу %s";
        return String.format(errorMessage, key);
    }

    @Override
    public String getChooseCurrency(DealType dealType) {
        return getMessage(DealType.BUY.equals(dealType)
                ? PropertiesMessage.CHOOSE_CURRENCY_BUY
                : PropertiesMessage.CHOOSE_CURRENCY_SELL);
    }
}
