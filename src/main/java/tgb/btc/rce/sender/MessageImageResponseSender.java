package tgb.btc.rce.sender;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.service.IMessageImageService;

import java.util.Optional;

@Service
public class MessageImageResponseSender implements IMessageImageResponseSender {


    private final IMessageImageService messageImageService;
    private final ResponseSender responseSender;
    private final IModifyUserService modifyUserService;

    @Autowired
    public MessageImageResponseSender(IMessageImageService messageImageService, ResponseSender responseSender, IModifyUserService modifyUserService) {
        this.messageImageService = messageImageService;
        this.responseSender = responseSender;
        this.modifyUserService = modifyUserService;
    }

    @Override
    public void sendMessage(MessageImage messageImage, Long chatId, String message, ReplyKeyboard replyKeyboard) {
        String fileId = messageImageService.getFileId(messageImage);
        if (StringUtils.isNotBlank(fileId)) {
            responseSender.sendPhoto(chatId, message, fileId, replyKeyboard);
        } else {
            responseSender.sendMessage(chatId, message, replyKeyboard, "html");
        }
    }

    @Override
    public void sendMessage(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard) {
        String fileId = messageImageService.getFileId(messageImage);
        String message = messageImageService.getMessage(messageImage);
        if (StringUtils.isNotBlank(fileId)) {
            responseSender.sendPhoto(chatId, message, fileId, replyKeyboard);
        } else {
            responseSender.sendMessage(chatId, message, replyKeyboard, "html");
        }
    }

    @Override
    public void sendMessageAndSaveMessageId(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard) {
        String fileId = messageImageService.getFileId(messageImage);
        String message = messageImageService.getMessage(messageImage);
        Optional<Message> optionalMessage;
        if (StringUtils.isNotBlank(fileId)) {
            optionalMessage = responseSender.sendPhoto(chatId, message, fileId, replyKeyboard);
        } else {
            optionalMessage = responseSender.sendMessage(chatId, message, replyKeyboard, "html");
        }
        optionalMessage
                .ifPresent(receviedMessage -> modifyUserService.updateBufferVariable(chatId, receviedMessage.getMessageId().toString()));
    }
}
