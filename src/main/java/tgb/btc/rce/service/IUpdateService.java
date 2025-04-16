package tgb.btc.rce.service;

import org.springframework.cache.annotation.Cacheable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

public interface IUpdateService {
    Long getChatId(Update update);

    Long getGroupChatId(Update update);

    boolean isGroupMessage(Update update);

    String getUsername(Update update);

    Integer getMessageId(Update update);

    boolean hasMessageText(Update update);

    String getMessageText(Update update);

    Long getLongFromText(Update update);

    BigDecimal getBigDecimalFromText(Update update);

    Message getMessage(Update update);

    boolean hasDocumentOrPhoto(Update update);
}
