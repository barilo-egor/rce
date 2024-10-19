package tgb.btc.rce.sender;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.MessageImage;

public interface IMessageImageResponseSender {
    void sendMessage(MessageImage messageImage, Long chatId, String message, ReplyKeyboard replyKeyboard);

    void sendMessage(MessageImage messageImage, Long chatId);

    void sendMessage(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard);

    void sendMessage(MessageImage messageImage, Menu menu, Long chatId);

    void sendMessageAndSaveMessageId(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard);
}
