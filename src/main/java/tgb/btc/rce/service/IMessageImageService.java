package tgb.btc.rce.service;

import org.springframework.cache.annotation.Cacheable;
import tgb.btc.rce.enums.MessageImage;

public interface IMessageImageService {

    String getFileId(MessageImage messageImage);

    @Cacheable("messageImageMessagesCache")
    String getMessage(MessageImage messageImage);

    @Cacheable("messageImageSubTypesCache")
    Integer getSubType(MessageImage messageImage);
}
