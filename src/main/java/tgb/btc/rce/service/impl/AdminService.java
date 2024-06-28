package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.interfaces.service.botbot.user.IReadUserService;
import tgb.btc.rce.enums.BotInlineButton;
import tgb.btc.rce.service.sender.ResponseSender;
import tgb.btc.rce.vo.InlineButton;

@Service
public class AdminService {

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

    public void notify(String message) {
        readUserService.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, message));
    }

    public void notify(String message, ReplyKeyboard replyKeyboard) {
        readUserService.getAdminsChatIds().forEach(chatId -> responseSender.sendMessage(chatId, message, replyKeyboard));
    }

    public void notify(String message, String data) {
        InlineButton button = BotInlineButton.SHOW.getButton();
        button.setData(data);
        readUserService.getAdminsChatIds().forEach(chatId ->
                responseSender.sendMessage(chatId, message, button));
    }
}
