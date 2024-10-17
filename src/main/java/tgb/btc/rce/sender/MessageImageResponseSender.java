package tgb.btc.rce.sender;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.service.IMessageImageService;

@Service
public class MessageImageResponseSender implements IMessageImageResponseSender {


    private final IMessageImageService messageImageService;
    private final ResponseSender responseSender;

    @Autowired
    public MessageImageResponseSender(IMessageImageService messageImageService, ResponseSender responseSender) {
        this.messageImageService = messageImageService;
        this.responseSender = responseSender;
    }

    @Override
    public void sendMessage(MessageImage messageImage, Long chatId, String message, ReplyKeyboard replyKeyboard) {
        String fileId = messageImageService.getFileId(messageImage);
        if (StringUtils.isNotBlank(fileId)) {
            responseSender.sendPhoto(chatId, message, fileId, replyKeyboard);
        } else {
            responseSender.sendMessage(chatId, message, replyKeyboard);
        }
    }

    @Override
    public void sendMessage(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard) {
        String fileId = messageImageService.getFileId(messageImage);
        String message = messageImageService.getMessage(messageImage);
        if (StringUtils.isNotBlank(fileId)) {
            responseSender.sendPhoto(chatId, message, fileId, replyKeyboard);
        } else {
            responseSender.sendMessage(chatId, message, replyKeyboard);
        }
    }
}
