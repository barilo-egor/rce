package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.service.properties.MessagePropertiesReader;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.util.IMessagePropertiesService;

import java.util.Objects;

@Service
public class MessagePropertiesService implements IMessagePropertiesService {
    
    private MessagePropertiesReader messagePropertiesReader;

    @Autowired
    public void setMessagePropertiesReader(MessagePropertiesReader messagePropertiesReader) {
        this.messagePropertiesReader = messagePropertiesReader;
    }

    @Override
    public String getMessage(PropertiesMessage message) {
        String text;
        try {
            String propertiesMessage = messagePropertiesReader.getString(message.getKey());
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
            String message = messagePropertiesReader.getString(key);
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
            String message = messagePropertiesReader.getString(key);
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
