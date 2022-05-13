package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.bean.BotMessage;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IResponseSender;

import java.util.Optional;

@Service
@Slf4j
public class ResponseSender implements IResponseSender {

    private RceBot bot;

    @Autowired
    public void setBot(RceBot bot) {
        this.bot = bot;
    }

    public Optional<Message> sendMessage(Long chatId, String text) {
        return Optional.of(executeSendMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build()));
    }

    public Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        return Optional.of(executeSendMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(replyKeyboard)
                .build()));
    }

    @Override
    public Optional<Message> sendMessage(SendMessage sendMessage) {
        return Optional.of(executeSendMessage(sendMessage));
    }

    private Message executeSendMessage(SendMessage sendMessage) {
        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Не получилось отправить sendMessage: " + sendMessage);
            throw new BaseException("Не получилось отправить sendMessage.");
        }
    }

    public Optional<Message> sendBotMessage(BotMessage botMessage, Long chatId) {
        return sendBotMessage(botMessage, chatId, null);
    }

    public Optional<Message> sendBotMessage(BotMessage botMessage, Long chatId, ReplyKeyboard replyKeyboard) {
        switch (botMessage.getMessageType()) {
            case TEXT:
                return sendMessage(chatId, botMessage.getText(), replyKeyboard);
            case IMAGE:
                return sendPhoto(chatId, botMessage.getText(), botMessage.getImage(), replyKeyboard);
            case ANIMATION:
                return sendAnimation(chatId, botMessage.getText(), botMessage.getAnimation(), replyKeyboard);
        }
        return Optional.empty();
    }

    public Optional<Message> sendPhoto(Long chatId, String caption, String photo, ReplyKeyboard replyKeyboard) {
        try {
            return Optional.of(bot.execute(SendPhoto.builder()
                    .chatId(chatId.toString())
                    .caption(caption)
                    .photo(new InputFile(photo))
                    .replyMarkup(replyKeyboard)
                    .build()));
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить фото: chatId=" + chatId + ", caption=" + caption + ", photo=" + photo, e);
            return Optional.empty();
        }
    }

    public Optional<Message> sendAnimation(Long chatId, String caption, String animation, ReplyKeyboard replyKeyboard) {
        try {
            return Optional.of(bot.execute(SendAnimation.builder()
                    .chatId(chatId.toString())
                    .caption(caption)
                    .animation(new InputFile(animation))
                    .replyMarkup(replyKeyboard)
                    .build()));
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить анимацию: chatId=" + chatId + ", caption="
                    + caption + ", animation=" + animation, e);
            return Optional.empty();
        }
    }

    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            bot.execute(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить измененное сообщение : chatId=" + chatId
                    + ", messageId=" + messageId, e);
        }
    }
}
