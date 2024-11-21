package tgb.btc.rce.service.handler.message;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.MessageType;

public interface IMessageHandler {

    boolean handleMessage(Message message);

    MessageType getMessageType();
}
