package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.BotInlineButton;
import tgb.btc.rce.vo.InlineButton;

import java.util.Set;

@Service
public class NotifyService {

    private IReadUserService readUserService;

    private ResponseSender responseSender;

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    public void notifyMessage(String message) {
        readUserService.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, message));
    }

    public void notifyMessage(String message, Set<UserRole> roles) {
        readUserService.getChatIdsByRoles(roles).forEach(chatId -> responseSender.sendMessage(chatId, message));
    }

    public void notifyMessageAndPhoto(String message, String imageId, Set<UserRole> roles) {
        readUserService.getChatIdsByRoles(roles).forEach(chatId -> responseSender.sendPhoto(chatId, message, imageId));
    }

    public void notifyMessage(String message, ReplyKeyboard replyKeyboard, Set<UserRole> roles) {
        readUserService.getChatIdsByRoles(roles).forEach(chatId -> responseSender.sendMessage(chatId, message, replyKeyboard));
    }

    public void notifyMessage(String message, String data, Set<UserRole> roles) {
        InlineButton button = BotInlineButton.SHOW.getButton();
        button.setData(data);
        readUserService.getChatIdsByRoles(roles).forEach(chatId ->
                responseSender.sendMessage(chatId, message, button));
    }
}
