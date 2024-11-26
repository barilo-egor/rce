package tgb.btc.rce.sender;

import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IResponseSender {

    Optional<Message> sendMessage(Long chatId, String text);

    Optional<Message> sendMessage(Long chatId, String text, Integer replyToMessageId);

    Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard);

    Optional<Message> sendMessage(Long chatId, String text, Menu menu);

    Optional<Message> sendMessage(Long chatId, PropertiesMessage propertiesMessage, Menu menu);

    Optional<Message> sendMessage(Long chatId, String text, String parseMode);

    Optional<Message> sendMessage(Long chatId, String text, List<InlineButton> buttons);

    Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard, String parseMode);

    Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard, String parseMode, Integer replyToMessageId);

    Optional<Message> sendMessage(Long chatId, String text, InlineButton... inlineButtons);

    Optional<Message> sendMessage(Long chatId, String text, String parseMode, InlineButton... inlineButtons);

    Optional<Message> sendMessage(SendMessage sendMessage);

    Optional<Message> sendPhoto(Long chatId, String caption, String photo);

    Optional<Message> sendPhoto(Long chatId, String caption, InputFile photo);

    Optional<Message> sendPhoto(Long chatId, String caption, String photo, ReplyKeyboard replyKeyboard);

    Message sendAnimation(Long chatId, File file);

    Optional<Message> sendAnimation(Long chatId, String caption, String animation, ReplyKeyboard replyKeyboard);

    void deleteMessage(Long chatId, Integer messageId);

    Message sendFile(Long chatId, File file);

    Message sendFile(Long chatId, String caption, String fileId);

    Optional<org.telegram.telegrambots.meta.api.objects.File> execute(GetFile getFile);

    void downloadFile(Document document, String localFilePath) throws IOException, TelegramApiException;

    void downloadFile(String fileId, String localFilePath);

    void execute(AnswerInlineQuery answerInlineQuery);

    Message execute(SendDice sendDice);

    void sendMedia(Long chatId, List<InputMedia> media);

    void sendInputFile(Long chatId, InputFile inputFile);

    boolean sendAnswerInlineQuery(String inlineQueryId, String title, String description, String messageText);

    boolean sendAnswerInlineQuery(String inlineQueryId, String title);

    void deleteCallbackMessageIfExists(Update update);

    void sendEditedMessageText(Long chatId, Integer messageId, String text);

    void sendEditedMessageText(Long chatId, Integer messageId, String text, ReplyKeyboard replyKeyboard);

    void deleteCallbackMessageButtonsIfExists(Update update);

    void sendAnswerCallbackQuery(String callbackQueryId, String text, boolean showAlert);
}
