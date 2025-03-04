package tgb.btc.rce.sender;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.interfaces.enums.MessageImage;
import tgb.btc.rce.enums.Menu;

import java.util.Optional;

public interface IMessageImageResponseSender {
    Optional<Message> sendMessage(MessageImage messageImage, Long chatId, String message, ReplyKeyboard replyKeyboard);

    void sendMessage(MessageImage messageImage, Long chatId);

    void sendMessage(MessageImage messageImage, Long chatId, ReplyKeyboard replyKeyboard);

    void sendMessage(MessageImage messageImage, Menu menu, Long chatId);
}
