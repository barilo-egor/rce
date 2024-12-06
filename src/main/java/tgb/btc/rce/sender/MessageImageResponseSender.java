package tgb.btc.rce.sender;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.MessageImage;
import tgb.btc.rce.service.IMessageImageService;
import tgb.btc.rce.service.redis.IRedisStringService;
import tgb.btc.rce.service.util.IMenuService;

@Service
public class MessageImageResponseSender implements IMessageImageResponseSender {


    private final IMessageImageService messageImageService;
    private final ResponseSender responseSender;
    private final IModifyUserService modifyUserService;
    private final IMenuService menuService;
    private final IReadUserService readUserService;
    private final IRedisStringService redisStringService;

    @Autowired
    public MessageImageResponseSender(IMessageImageService messageImageService, ResponseSender responseSender,
                                      IModifyUserService modifyUserService, IMenuService menuService,
                                      IReadUserService readUserService, IRedisStringService redisStringService) {
        this.messageImageService = messageImageService;
        this.responseSender = responseSender;
        this.modifyUserService = modifyUserService;
        this.menuService = menuService;
        this.readUserService = readUserService;
        this.redisStringService = redisStringService;
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
    public void sendMessage(MessageImage messageImage, Long chatId) {
        sendMessage(messageImage, chatId, null);
    }

    @Override
    public void sendMessage(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard) {
        String fileId = messageImageService.getFileId(messageImage);
        String message = messageImageService.getMessage(messageImage);
        if (StringUtils.isNotBlank(fileId)) {
            String format = messageImageService.getFormat(messageImage);
            if (format.equals(".jpg") || format.equals(".png") || format.equals(".jpeg")) {
                responseSender.sendPhoto(chatId, message, fileId, replyKeyboard);
            } else if (format.equals(".mp4")) {
                responseSender.sendAnimation(chatId, message, fileId, replyKeyboard);
            }
        } else {
            responseSender.sendMessage(chatId, message, replyKeyboard, "html");
        }
    }

    @Override
    public void sendMessage(MessageImage messageImage, Menu menu, Long chatId) {
        sendMessage(messageImage, chatId, menuService.build(menu, readUserService.getUserRoleByChatId(chatId)));
    }
}
