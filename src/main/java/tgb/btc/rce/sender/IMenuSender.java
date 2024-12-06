package tgb.btc.rce.sender;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;

import java.util.Optional;

public interface IMenuSender {
    Optional<Message> send(Long chatId, PropertiesMessage propertiesMessage, Menu menu);

    Optional<Message> send(Long chatId, String text, Menu menu);
}
