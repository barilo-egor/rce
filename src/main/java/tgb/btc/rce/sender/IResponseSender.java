package tgb.btc.rce.sender;

import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IResponseSender {

    Optional<Message> sendMessage(Long chatId, String text);

    Optional<Message> sendMessage(Long chatId, String text, Integer replyToMessageId);

    Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard);

    Optional<Message> sendMessage(Long chatId, String text, InlineButton... inlineButtons);

    Optional<Message> sendMessage(Long chatId, String text, List<InlineButton> buttons);

    Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard, Integer replyToMessageId);

    void sendMessageThrows(Long chatId, String text) throws TelegramApiException;

    Optional<Message> sendPhoto(Long chatId, String caption, InputFile photo);

    Optional<Message> sendPhoto(Long chatId, String caption, String photo, ReplyKeyboard replyKeyboard);

    Optional<Message> sendPhoto(Long chatId, String caption, String photo);

    Optional<Message> sendPhoto(Long chatId, String caption, InputFile photo, ReplyKeyboard replyKeyboard);

    Message sendAnimation(Long chatId, File file);

    void sendAnimation(Long chatId, String caption, String animation, ReplyKeyboard replyKeyboard);

    Message sendAnimation(Long chatId, InputFile inputFile, String caption, ReplyKeyboard replyKeyboard);

    void deleteMessage(Long chatId, Integer messageId);

    void sendEditedMessageText(Long chatId, Integer messageId, String text);

    void sendEditedMessageText(Long chatId, Integer messageId, String text, List<InlineButton> buttons);

    void sendEditedMessageText(Long chatId, Integer messageId, String text, ReplyKeyboard replyKeyboard);

    void sendEditedMessageText(Long chatId, Integer messageId, String text, InlineKeyboardMarkup keyboard);

    Message sendFile(Long chatId, String caption, String fileId);

    Message sendFile(Long chatId, File file);

    Message sendFile(Long chatId, InputFile inputFile);

    Message sendFile(Long chatId, InputFile inputFile, String caption);

    void downloadFile(Document document, String localFilePath) throws IOException;

    void sendAnswerInlineQuery(String inlineQueryId, String title, String description, String messageText);

    void deleteCallbackMessageIfExists(Update update);

    void sendAnswerCallbackQuery(String callbackQueryId, String text, boolean showAlert);
}
