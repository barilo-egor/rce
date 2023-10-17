package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.impl.bean.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

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
        userService.getAdminsChatIds().forEach(chatId ->
                responseSender.sendMessage(chatId, message, KeyboardUtil.buildInline(List.of(InlineButton.builder()
                        .text(BotStringConstants.SHOW_BUTTON)
                        .data(data)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build()))));
    }
}
