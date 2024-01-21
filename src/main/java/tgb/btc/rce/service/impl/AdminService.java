package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.service.bean.bot.UserService;
import tgb.btc.rce.enums.BotInlineButton;
import tgb.btc.rce.vo.InlineButton;

@Service
public class AdminService {

    private final UserService userService;
    private final ResponseSender responseSender;

    @Autowired
    public AdminService(UserService userService, ResponseSender responseSender) {
        this.userService = userService;
        this.responseSender = responseSender;
    }

    public void notify(String message) {
        userService.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, message));
    }

    public void notify(String message, ReplyKeyboard replyKeyboard) {
        userService.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, message, replyKeyboard));
    }

    public void notify(String message, String data) {
        InlineButton button = BotInlineButton.SHOW.getButton();
        button.setData(data);
        userService.getAdminsChatIds().forEach(chatId ->
                responseSender.sendMessage(chatId, message, button));
    }
}
