package tgb.btc.rce.service.handler.impl.message.text;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextMessageType;
import tgb.btc.rce.service.handler.message.text.ISimpleTextHandler;
import tgb.btc.rce.service.handler.message.text.ITextMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SimpleTextHandler implements ITextMessageHandler {

    private final Map<String, ISimpleTextHandler> simpleTextHandlerMap;

    public SimpleTextHandler(List<ISimpleTextHandler> simpleTextHandlers) {
        this.simpleTextHandlerMap = new HashMap<>();
        for (ISimpleTextHandler simpleTextHandler : simpleTextHandlers) {
            this.simpleTextHandlerMap.put(simpleTextHandler.getText(), simpleTextHandler);
        }
    }

    @Override
    public boolean handle(Message message) {
        ISimpleTextHandler simpleTextHandler = simpleTextHandlerMap.get(message.getText());
        if (Objects.isNull(simpleTextHandler)) {
            return false;
        }
        simpleTextHandler.handle(message);
        return true;
    }

    @Override
    public TextMessageType getTextMessageType() {
        return TextMessageType.SIMPLE_TEXT;
    }
}
