package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.bean.BotMessage;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.IResponseSender;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
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


    public Optional<Message> sendMessageThrows(Long chatId, String text) {
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

    public void sendEditedMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        try {
            bot.execute(EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить измененное сообщение: chatId" + chatId + ", text=" + text, e);
        }
    }

    public void sendFile(Long chatId, File file) {
        try {
            bot.execute(SendDocument.builder()
                    .chatId(chatId.toString())
                    .document(new InputFile(file))
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить файл: chatId=" + chatId + ", fileName=" + file.getName());
        }
    }

    public Optional<org.telegram.telegrambots.meta.api.objects.File> execute(GetFile getFile) {
        try {
            return Optional.of(bot.execute(getFile));
        } catch (TelegramApiException e) {
            log.debug("Не получилось скачать файл:" + getFile.toString());
            return Optional.empty();
        }
    }

    public void downloadFile(Document document, String localFilePath) throws IOException, TelegramApiException {
        org.telegram.telegrambots.meta.api.objects.File file = getFilePath(document);

        java.io.File localFile = new java.io.File(localFilePath);
        InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
    }

    private org.telegram.telegrambots.meta.api.objects.File getFilePath(Document document) throws TelegramApiException {
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        return execute(getFile).get();
    }
}
