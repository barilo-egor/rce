package tgb.btc.rce.sender;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
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
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.library.exception.BaseException;
import tgb.btc.rce.bot.RceBot;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ResponseSender implements IResponseSender {

    private final RceBot bot;
    private final IKeyboardBuildService keyboardBuildService;
    private final String botToken;

    public ResponseSender(RceBot bot, IKeyboardBuildService keyboardBuildService, @Value("${bot.token}") String botToken) {
        this.bot = bot;
        this.keyboardBuildService = keyboardBuildService;
        this.botToken = botToken;
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text) {
        return sendMessage(chatId, text, null, null);
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, Integer replyToMessageId) {
        return sendMessage(chatId, text, null, replyToMessageId);
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        return sendMessage(chatId, text, replyKeyboard, null);
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, InlineButton... inlineButtons) {
        return sendMessage(chatId, text, keyboardBuildService.buildInline(List.of(inlineButtons)), null);
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, ReplyButton... replyButtons) {
        return sendMessage(chatId, text, keyboardBuildService.buildReply(List.of(replyButtons)), null);
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, List<InlineButton> buttons) {
        return sendMessage(chatId, text, keyboardBuildService.buildInline(buttons), null);
    }

    @Override
    public Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard, Integer replyToMessageId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyToMessageId(replyToMessageId)
                .replyMarkup(replyKeyboard)
                .parseMode("html")
                .build();
        return Optional.ofNullable(executeSendMessage(sendMessage));
    }

    private Message executeSendMessage(SendMessage sendMessage) {
        try {
            sendMessage.setParseMode("html");
            return bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            return null;
        }
    }

    @Override
    public void sendMessageThrows(Long chatId, String text) throws TelegramApiException {
        bot.execute(SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .parseMode("html")
                .build());
    }

    @Override
    public Optional<Message> sendPhoto(Long chatId, String caption, InputFile photo) {
        return sendPhoto(chatId, caption, photo, null);
    }

    @Override
    public Optional<Message> sendPhoto(Long chatId, String caption, String photo, ReplyKeyboard replyKeyboard) {
        return sendPhoto(chatId, caption, new InputFile(photo), replyKeyboard);
    }

    @Override
    public Optional<Message> sendPhoto(Long chatId, String caption, String photo) {
        return sendPhoto(chatId, caption, new InputFile(photo), null);
    }

    @Override
    public Optional<Message> sendPhoto(Long chatId, String caption, InputFile photo, ReplyKeyboard replyKeyboard) {
        try {
            return Optional.ofNullable(bot.execute(SendPhoto.builder()
                    .chatId(chatId.toString())
                    .caption(caption)
                    .photo(photo)
                    .replyMarkup(replyKeyboard)
                    .parseMode("html")
                    .build()));
        } catch (TelegramApiException e) {
            return Optional.empty();
        }
    }

    @Override
    public Message sendAnimation(Long chatId, File file) {
        return sendAnimation(chatId, new InputFile(file), null, null);
    }

    @Override
    public Optional<Message> sendAnimation(Long chatId, String caption, String animation, ReplyKeyboard replyKeyboard) {
        return Optional.ofNullable(sendAnimation(chatId, new InputFile(animation), caption, replyKeyboard));
    }

    @Override
    public Message sendAnimation(Long chatId, InputFile inputFile, String caption, ReplyKeyboard replyKeyboard) {
        try {
            return bot.execute(SendAnimation.builder()
                    .chatId(chatId.toString())
                    .animation(inputFile)
                    .caption(caption)
                    .replyMarkup(replyKeyboard)
                    .parseMode("html")
                    .build());
        } catch (TelegramApiException e) {
            return null;
        }
    }

    @Override
    public void deleteMessage(Long chatId, Integer messageId) {
        try {
            bot.execute(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .build());
        } catch (TelegramApiException ignored) {
        }
    }

    @Override
    public void sendEditedMessageText(Long chatId, Integer messageId, String text) {
        sendEditedMessageText(chatId, messageId, text, (InlineKeyboardMarkup) null);
    }

    @Override
    public void sendEditedMessageText(Long chatId, Integer messageId, String text, List<InlineButton> buttons) {
        sendEditedMessageText(chatId, messageId, text, keyboardBuildService.buildInline(buttons));
    }

    @Override
    public void sendEditedMessageText(Long chatId, Integer messageId, String text, ReplyKeyboard replyKeyboard) {
        sendEditedMessageText(chatId, messageId, text, (InlineKeyboardMarkup) replyKeyboard);
    }

    @Override
    public void sendEditedMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard) {
        try {
            bot.execute(EditMessageText.builder()
                    .chatId(chatId.toString())
                    .messageId(messageId)
                    .text(text)
                    .replyMarkup(keyboard)
                    .parseMode("html")
                    .build());
        } catch (TelegramApiException ignored) {
        }
    }

    @Override
    public Message sendFile(Long chatId, String caption, String fileId) {
        return sendFile(chatId, new InputFile(fileId), caption);
    }

    @Override
    public Message sendFile(Long chatId, File file) {
        return sendFile(chatId, new InputFile(file), null);
    }

    @Override
    public Message sendFile(Long chatId, InputFile inputFile) {
        return sendFile(chatId, inputFile, null);
    }

    @Override
    public Message sendFile(Long chatId, InputFile inputFile, String caption) {
        return sendFile(chatId, inputFile, caption, null);
    }

    @Override
    public Message sendFile(Long chatId, InputFile inputFile, String caption, ReplyKeyboard replyKeyboard) {
        try {
            return bot.execute(SendDocument.builder()
                    .chatId(chatId.toString())
                    .document(inputFile)
                    .caption(caption)
                    .parseMode("html")
                    .replyMarkup(replyKeyboard)
                    .build());
        } catch (TelegramApiException e) {
            return null;
        }
    }

    @Override
    public void downloadFile(Document document, String localFilePath) throws IOException, URISyntaxException {
        org.telegram.telegrambots.meta.api.objects.File file = getFilePath(document);
        java.io.File localFile = new java.io.File(localFilePath);
        URI uri = new URI(file.getFileUrl(botToken));
        InputStream is = uri.toURL().openStream();
        FileUtils.copyInputStreamToFile(is, localFile);
    }

    private org.telegram.telegrambots.meta.api.objects.File getFilePath(Document document) {
        GetFile getFile = new GetFile();
        getFile.setFileId(document.getFileId());
        return execute(getFile);
    }

    private org.telegram.telegrambots.meta.api.objects.File execute(GetFile getFile) {
        try {
            return bot.execute(getFile);
        } catch (TelegramApiException e) {
            throw new BaseException("Не получилось сказать файл: " + getFile, e);
        }
    }

    @Override
    public void sendAnswerInlineQuery(String inlineQueryId, String title, String description, String messageText) {
        try {
            bot.execute(AnswerInlineQuery.builder().inlineQueryId(inlineQueryId)
                    .result(InlineQueryResultArticle.builder()
                            .id(inlineQueryId)
                            .title(title)
                            .inputMessageContent(InputTextMessageContent.builder()
                                    .messageText(messageText)
                                    .build())
                            .description(description)
                            .build())
                    .build());
        } catch (TelegramApiException ignored) {
        }
    }

    @Override
    public void deleteCallbackMessageIfExists(Update update) {
        Long chatId = UpdateType.getChatId(update);
        if (update.hasCallbackQuery())
            deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
    }

    @Override
    public void sendAnswerCallbackQuery(String callbackQueryId, String text, boolean showAlert) {
        try {
            bot.execute(AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQueryId)
                    .text(text)
                    .showAlert(showAlert)
                    .build());
        } catch (TelegramApiException ignored) {
        }
    }

}
