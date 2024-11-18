package tgb.btc.rce.service.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.update.MessageType;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.handler.IUpdateHandler;
import tgb.btc.rce.service.handler.message.IMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MessageUpdateHandler implements IUpdateHandler {

    private final Map<MessageType, IMessageHandler> messageHandlerMap = new HashMap<>();
    public MessageUpdateHandler(List<IMessageHandler> messageHandlers) {
        for (IMessageHandler messageHandler : messageHandlers) {
            messageHandlerMap.put(messageHandler.getMessageType(), messageHandler);
        }
    }

    @Override
    public void handle(Update update) {
        Message message = update.getMessage();
        MessageType messageType = MessageType.fromMessage(message);
        IMessageHandler messageHandler = messageHandlerMap.get(messageType);
        if (Objects.nonNull(messageHandler)) {
            messageHandler.handleMessage(message);
        }
    }

    @Override
    public UpdateType getUpdateType() {
        return UpdateType.MESSAGE;
    }
}
