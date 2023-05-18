package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.bean.BotMessage;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ResponseSender implements IResponseSender {

    private RceBot bot;

    private BotMessageService botMessageService;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Autowired
    public void setBot(RceBot bot) {
        this.bot = bot;
    }

    public Optional<Message> sendMessage(Long chatId, String text) {
        return Optional.ofNullable(executeSendMessage(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build()));
    }


    public Optional<Message> sendMessageThrows(Long chatId, String text) throws TelegramApiException {
        return Optional.ofNullable(executeSendMessageThrows(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build()));
    }

    public Optional<Message> sendMessage(Long chatId, String text, Menu menu) {
        return sendMessage(chatId, text, MenuFactory.build(menu, userRepository.isAdminByChatId(chatId)), null);
    }

    public Optional<Message> sendMessage(Long chatId, PropertiesMessage propertiesMessage, Menu menu) {
        return sendMessage(chatId, MessagePropertiesUtil.getMessage(propertiesMessage), menu);
    }

    public Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        return sendMessage(chatId, text, replyKeyboard, null);
    }

    public Optional<Message> sendMessage(Long chatId, String text, String parseMode) {
        return sendMessage(chatId, text, null, parseMode);
    }

    public Optional<Message> sendMessage(Long chatId, String text, InlineButton... inlineButtons) {
        return sendMessage(chatId, text, KeyboardUtil.buildInline(List.of(inlineButtons)));
    }

    public Optional<Message> sendMessage(Long chatId, String text, BotKeyboard botKeyboard) {
        return sendMessage(chatId, text, botKeyboard.getKeyboard(), null);
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard, String parseMode) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(replyKeyboard)
                .build();
        if (Strings.isNotEmpty(parseMode)) sendMessage.setParseMode(parseMode);
        return Optional.ofNullable(executeSendMessage(sendMessage));
    }

    @Override
    public Optional<Message> sendMessage(SendMessage sendMessage) {
        return Optional.ofNullable(executeSendMessage(sendMessage));
    }

    private Message executeSendMessage(SendMessage sendMessage) {
        try {
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn("Не получилось отправить sendMessage: " + sendMessage);
            return null;
        }
    }

    private Message executeSendMessageThrows(SendMessage sendMessage) throws TelegramApiException {
        return bot.execute(sendMessage);
    }

    public Optional<Message> sendBotMessage(BotMessageType botMessageType, Long chatId) {
        return sendBotMessage(botMessageService.findByType(botMessageType), chatId, null);
    }

    public Optional<Message> sendBotMessage(BotMessage botMessage, Long chatId) {
        return sendBotMessage(botMessage, chatId, null);
    }

    public Optional<Message> sendBotMessage(BotMessageType botMessageType, Long chatId, Menu menu) {
        return sendBotMessage(botMessageType, chatId, MenuFactory.build(menu, userRepository.isAdminByChatId(chatId)));
    }

    public Optional<Message> sendBotMessage(BotMessageType botMessageType, Long chatId, ReplyKeyboard replyKeyboard) {
        BotMessage botMessage = botMessageService.findByType(botMessageType);
        return sendBotMessage(botMessage, chatId, replyKeyboard);
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

    public Optional<Message> sendPhoto(Long chatId, String caption, String photo) {
        return sendPhoto(chatId, caption, photo, null);
    }

    public Optional<Message> sendPhoto(Long chatId, String caption, InputFile photo) {
        try {
            return Optional.of(bot.execute(SendPhoto.builder()
                    .chatId(chatId.toString())
                    .caption(caption)
                    .photo(photo)
                    .build()));
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить фото: chatId=" + chatId + ", caption=" + caption + ", photo=" + photo, e);
            return Optional.empty();
        }
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

    public void downloadFile(Document document, String localFilePath) throws IOException{
        org.telegram.telegrambots.meta.api.objects.File file = getFilePath(document);

        java.io.File localFile = new java.io.File(localFilePath);
        InputStream is = new URL(file.getFileUrl(bot.getBotToken())).openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
    }

    private org.telegram.telegrambots.meta.api.objects.File getFilePath(Document document) {
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        // TODO проверить optional
        return execute(getFile).get();
    }

    @Override
    public void execute(AnswerInlineQuery answerInlineQuery) {
        try {
            bot.execute(answerInlineQuery);
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить answerInlineQuery: " + answerInlineQuery.toString(), e);
        }
    }

    @Override
    public void sendMedia(Long chatId, List<InputMedia> media) {
        try {
            bot.execute(SendMediaGroup.builder()
                    .chatId(chatId.toString())
                    .medias(media)
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить медиа: chatId=" + chatId + ", media.size()=" + media.size());
        }
    }

    @Override
    public void sendInputFile(Long chatId, InputFile inputFile) {
        try {
            bot.execute(SendDocument.builder()
                    .chatId(chatId.toString())
                    .document(inputFile)
                    .build());
        } catch (TelegramApiException e) {
            log.debug("Не получилось отправить input file: chatId=" + chatId);
        }
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, MessageTemplate messageTemplate) {
        return sendMessage(chatId, messageTemplate.getMessage(), messageTemplate.getBotKeyboard());
    }
}
