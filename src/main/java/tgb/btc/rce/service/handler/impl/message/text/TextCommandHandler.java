package tgb.btc.rce.service.handler.impl.message.text;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.TextMessageType;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.handler.message.text.ITextMessageHandler;
import tgb.btc.rce.service.handler.util.ITextCommandService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TextCommandHandler implements ITextMessageHandler {

    private final ITextCommandService textCommandService;

    private final Map<TextCommand, ITextCommandHandler> textCommandHandlerMap = new HashMap<>();

    public TextCommandHandler(List<ITextCommandHandler> commandHandlers, ITextCommandService textCommandService) {
        this.textCommandService = textCommandService;
        for (ITextCommandHandler commandHandler : commandHandlers) {
            textCommandHandlerMap.put(commandHandler.getTextCommand(), commandHandler);
        }
    }

    @Override
    public void handle(Message message) {
        TextCommand textCommand = textCommandService.fromText(message.getText());
        if (Objects.nonNull(textCommand)) {
            ITextCommandHandler handler = textCommandHandlerMap.get(textCommand);
            if (Objects.nonNull(handler)) {
                handler.handle(message);
            }
        }
    }

    @Override
    public TextMessageType getTextMessageType() {
        return TextMessageType.TEXT_COMMAND;
    }
}
