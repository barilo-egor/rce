package tgb.btc.rce.service.handler.message.text;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextMessageType;

public interface ITextMessageHandler {

    void handle(Message message);

    TextMessageType getTextMessageType();
}
