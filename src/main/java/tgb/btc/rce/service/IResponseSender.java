package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import tgb.btc.rce.bean.BotMessage;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.MessageTemplate;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IResponseSender {

    Optional<Message> sendMessage(Long chatId, String text);

    Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard);

    Optional<Message> sendMessage(Long chatId, String text, InlineButton... inlineButtons);

    Optional<Message> sendMessage(Long chatId, String text, BotKeyboard botKeyboard);

    Optional<Message> sendMessage(SendMessage sendMessage);

    Optional<Message> sendPhoto(Long chatId, String caption, String photo);

    Optional<Message> sendPhoto(Long chatId, String caption, InputFile photo);

    Optional<Message> sendPhoto(Long chatId, String caption, String photo, ReplyKeyboard replyKeyboard);

    Optional<Message> sendAnimation(Long chatId, String caption, String animation, ReplyKeyboard replyKeyboard);

    Optional<Message> sendBotMessage(BotMessage botMessage, Long chatId, ReplyKeyboard replyKeyboard);

    Optional<Message> sendBotMessage(BotMessage botMessage, Long chatId);

    void deleteMessage(Long chatId, Integer messageId);

    void sendFile(Long chatId, File file);

    Optional<org.telegram.telegrambots.meta.api.objects.File> execute(GetFile getFile);

    void downloadFile(Document document, String localFilePath) throws IOException, TelegramApiException;

    void execute(AnswerInlineQuery answerInlineQuery);

    void sendMedia(Long chatId, List<InputMedia> media);

    void sendInputFile(Long chatId, InputFile inputFile);

    Optional<Message> sendMessage(Long chatId, MessageTemplate messageTemplate);
}
