package tgb.btc.rce.service.handler.impl.message;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.MessageType;
import tgb.btc.rce.enums.update.TextMessageType;
import tgb.btc.rce.service.handler.message.IMessageHandler;
import tgb.btc.rce.service.handler.message.text.ITextMessageHandler;
import tgb.btc.rce.service.handler.util.ITextMessageTypeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TextMessageHandler implements IMessageHandler {

    private final ITextMessageTypeService textMessageTypeService;

    private final Map<TextMessageType, ITextMessageHandler> textMessageHandlersMap = new HashMap<>();

    public TextMessageHandler(List<ITextMessageHandler> textMessageHandlers,
                              ITextMessageTypeService textMessageTypeService) {
        this.textMessageTypeService = textMessageTypeService;
        for (ITextMessageHandler textMessageHandler : textMessageHandlers) {
            textMessageHandlersMap.put(textMessageHandler.getTextMessageType(), textMessageHandler);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        TextMessageType textMessageType = textMessageTypeService.fromText(message.getText());
        if (Objects.nonNull(textMessageType)) {
            ITextMessageHandler textMessageHandler = textMessageHandlersMap.get(textMessageType);
            if (textMessageHandler != null) {
                return textMessageHandler.handle(message);
            }
        }
        return false;
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.TEXT;
    }

}
