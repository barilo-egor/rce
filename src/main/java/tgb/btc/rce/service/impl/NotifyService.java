package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class NotifyService implements INotifyService {

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

    @Override
    public void notifyMessage(String message, Set<UserRole> roles) {
        notifyMessage(message, roles, false);
    }

    @Override
    public void notifyMessage(String message, Set<UserRole> roles, boolean withExclude) {
        List<Long> excludeChatIds;
        if (withExclude) {
            excludeChatIds = readUserService.getChatIdsByIsNotificationsOn(false);
        } else {
            excludeChatIds = new ArrayList<>();
        }
        readUserService.getChatIdsByRoles(roles)
                .stream()
                .filter(chatId -> !excludeChatIds.contains(chatId))
                .forEach(chatId -> responseSender.sendMessage(chatId, message));
    }

    @Override
    public void notifyMessage(String message, Set<UserRole> roles, List<Long> excludeChatIds) {
        excludeChatIds.addAll(readUserService.getChatIdsByIsNotificationsOn(false));
        readUserService.getChatIdsByRoles(roles).stream()
                .filter(chatId -> !excludeChatIds.contains(chatId))
                .forEach(chatId -> responseSender.sendMessage(chatId, message));
    }

    @Override
    public void notifyMessageAndPhoto(String message, String imageId, Set<UserRole> roles) {
        List<Long> excludeChatIds = readUserService.getChatIdsByIsNotificationsOn(false);
        readUserService.getChatIdsByRoles(roles)
                .stream()
                .filter(chatId -> !excludeChatIds.contains(chatId))
                .forEach(chatId -> responseSender.sendPhoto(chatId, message, imageId));
    }

    @Override
    public void notifyMessage(String message, ReplyKeyboard replyKeyboard, Set<UserRole> roles) {
        List<Long> excludeChatIds = readUserService.getChatIdsByIsNotificationsOn(false);
        readUserService.getChatIdsByRoles(roles).stream()
                .filter(chatId -> !excludeChatIds.contains(chatId))
                .forEach(chatId -> responseSender.sendMessage(chatId, message, replyKeyboard));
    }

    @Override
    public void notifyMessage(String message, String data, Set<UserRole> roles) {
        InlineButton button = InlineButton.builder()
                .text("Показать")
                .inlineType(InlineType.CALLBACK_DATA)
                .build();
        button.setData(data);
        List<Long> excludeChatIds = readUserService.getChatIdsByIsNotificationsOn(false);
        readUserService.getChatIdsByRoles(roles)
                .stream()
                .filter(chatId -> !excludeChatIds.contains(chatId))
                .forEach(chatId ->
                        responseSender.sendMessage(chatId, message, button));
    }
}
