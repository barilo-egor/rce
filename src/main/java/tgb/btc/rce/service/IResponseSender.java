package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.BotMessage;

import java.util.Optional;

public interface IResponseSender {

    Optional<Message> sendMessage(Long chatId, String text);

    Optional<Message> sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard);

    Optional<Message> sendMessage(SendMessage sendMessage);

    Optional<Message> sendPhoto(Long chatId, String caption, String photo, ReplyKeyboard replyKeyboard);

    Optional<Message> sendAnimation(Long chatId, String caption, String animation, ReplyKeyboard replyKeyboard);

    Optional<Message> sendBotMessage(BotMessage botMessage, Long chatId, ReplyKeyboard replyKeyboard);

    Optional<Message> sendBotMessage(BotMessage botMessage, Long chatId);

    void deleteMessage(Long chatId, Integer messageId);
}
