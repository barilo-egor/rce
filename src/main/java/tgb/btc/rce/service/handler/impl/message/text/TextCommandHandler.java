package tgb.btc.rce.service.handler.impl.message.text;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
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

    private final IReadUserService readUserService;

    private final Map<TextCommand, ITextCommandHandler> textCommandHandlerMap = new HashMap<>();

    public TextCommandHandler(List<ITextCommandHandler> commandHandlers, ITextCommandService textCommandService,
                              IReadUserService readUserService) {
        this.textCommandService = textCommandService;
        this.readUserService = readUserService;
        for (ITextCommandHandler commandHandler : commandHandlers) {
            textCommandHandlerMap.put(commandHandler.getTextCommand(), commandHandler);
        }
    }

    @Override
    public boolean handle(Message message) {
        TextCommand textCommand = textCommandService.fromText(message.getText());
        if (Objects.isNull(textCommand)) {
            return false;
        }
        UserRole userRole = readUserService.getUserRoleByChatId(message.getFrom().getId());
        if (!textCommand.hasAccess(userRole)) {
            return false;
        }
        ITextCommandHandler handler = textCommandHandlerMap.get(textCommand);
        if (Objects.nonNull(handler)) {
            handler.handle(message);
            return true;
        }
        return false;
    }

    @Override
    public TextMessageType getTextMessageType() {
        return TextMessageType.TEXT_COMMAND;
    }
}
