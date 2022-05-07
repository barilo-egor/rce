package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface IResponseSender {

    Message sendMessage(Long chatId, String text);

    Message sendMessage(Long chatId, String text, ReplyKeyboard replyKeyboard);

    Message sendMessage(SendMessage sendMessage);
}
