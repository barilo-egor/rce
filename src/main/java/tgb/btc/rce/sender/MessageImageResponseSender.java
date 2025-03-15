package tgb.btc.rce.sender;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.enums.MessageImage;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.design.IMessageImageService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.util.IMenuService;

import java.util.Optional;

@Service
public class MessageImageResponseSender implements IMessageImageResponseSender {


    private final IMessageImageService messageImageService;
    private final ResponseSender responseSender;
    private final IMenuService menuService;
    private final IReadUserService readUserService;

    @Autowired
    public MessageImageResponseSender(IMessageImageService messageImageService, ResponseSender responseSender,
                                      IMenuService menuService, IReadUserService readUserService) {
        this.messageImageService = messageImageService;
        this.responseSender = responseSender;
        this.menuService = menuService;
        this.readUserService = readUserService;
    }

    @Override
    public Optional<Message> sendMessage(MessageImage messageImage, Long chatId, String message, ReplyKeyboard replyKeyboard) {
        String fileId = messageImageService.getFileId(messageImage);
        if (StringUtils.isNotBlank(fileId)) {
            return responseSender.sendPhoto(chatId, message, fileId, replyKeyboard);
        } else {
            return responseSender.sendMessage(chatId, message, replyKeyboard);
        }
    }

    @Override
    public Optional<Message> sendMessage(MessageImage messageImage, Long chatId) {
        return sendMessage(messageImage, chatId, null);
    }

    @Override
    public Optional<Message> sendMessage(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard) {
        String fileId = messageImageService.getFileId(messageImage);
        String message = messageImageService.getMessage(messageImage);
        if (StringUtils.isNotBlank(fileId)) {
            String format = messageImageService.getFormat(messageImage);
            if (format.equals(".jpg") || format.equals(".png") || format.equals(".jpeg")) {
                return responseSender.sendPhoto(chatId, message, fileId, replyKeyboard);
            } else if (format.equals(".mp4")) {
                return responseSender.sendAnimation(chatId, message, fileId, replyKeyboard);
            } else {
                throw new BaseException("Для формата " + format + " не предусмотрена реализация отправки изображения.");
            }
        } else {
            return responseSender.sendMessage(chatId, message, replyKeyboard);
        }
    }

    @Override
    public void sendMessage(MessageImage messageImage, Menu menu, Long chatId) {
        sendMessage(messageImage, chatId, menuService.build(menu, readUserService.getUserRoleByChatId(chatId)));
    }
}
