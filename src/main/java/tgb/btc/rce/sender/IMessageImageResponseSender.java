package tgb.btc.rce.sender;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.interfaces.enums.MessageImage;
import tgb.btc.rce.enums.Menu;

public interface IMessageImageResponseSender {
    void sendMessage(MessageImage messageImage, Long chatId, String message, ReplyKeyboard replyKeyboard);

    void sendMessage(MessageImage messageImage, Long chatId);

    void sendMessage(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard);

    void sendMessage(MessageImage messageImage, Menu menu, Long chatId);
}
