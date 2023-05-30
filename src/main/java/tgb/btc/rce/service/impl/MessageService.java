package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Service
public class MessageService {

    private ResponseSender responseSender;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    public void sendMessageAndSaveMessageId(Long chatId, String text, ReplyKeyboard keyboard) {
        responseSender.sendMessage(chatId, text, keyboard)
                .ifPresent(message -> userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }

    public void sendMessageAndSaveMessageId(SendMessage sendMessage) {
        responseSender.sendMessage(sendMessage)
                .ifPresent(message -> userService.updateBufferVariable(Long.parseLong(sendMessage.getChatId()), message.getMessageId().toString()));
    }
}
