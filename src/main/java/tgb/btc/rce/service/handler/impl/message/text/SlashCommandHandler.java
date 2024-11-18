package tgb.btc.rce.service.handler.impl.message.text;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextMessageType;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;
import tgb.btc.rce.service.handler.message.text.ITextMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SlashCommandHandler implements ITextMessageHandler {

    private final Map<String, ISlashCommandHandler> handlers = new HashMap<>();

    public SlashCommandHandler(List<ISlashCommandHandler> slashCommandHandlers) {
        for (ISlashCommandHandler handler : slashCommandHandlers) {
            handlers.put(handler.getSlashCommand(), handler);
        }
    }

    public void handle(Message message) {
        String text = message.getText();
        int spaceIndex = text.indexOf(" ");
        ISlashCommandHandler slashCommandHandler;
        if (spaceIndex == -1) {
            slashCommandHandler = handlers.get(text);
        } else {
            slashCommandHandler = handlers.get(text.substring(0, spaceIndex));
        }
        if (Objects.nonNull(slashCommandHandler)) {
            slashCommandHandler.handle(message);
        }
    }

    @Override
    public TextMessageType getTextMessageType() {
        return TextMessageType.SLASH;
    }
}
