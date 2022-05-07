package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IResponseSender;

@Service
@Slf4j
public class ResponseSender implements IResponseSender {

    private RceBot bot;

    @Autowired
    public void setBot(RceBot bot) {
        this.bot = bot;
    }

    public Message sendMessage(Long chatId, String text) {
        return executeSendMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build());
    }

    public Message sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        return executeSendMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(replyKeyboard)
                .build());
    }

    @Override
    public Message sendMessage(SendMessage sendMessage) {
        return executeSendMessage(sendMessage);
    }

    private Message executeSendMessage(SendMessage sendMessage) {
        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Не получилось отправить sendMessage: " + sendMessage);
            throw new BaseException("Не получилось отправить sendMessage.");
        }
    }
}
