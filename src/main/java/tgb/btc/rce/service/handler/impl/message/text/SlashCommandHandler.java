package tgb.btc.rce.service.handler.impl.message.text;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.enums.update.TextMessageType;
import tgb.btc.rce.service.enums.ISlashCommandService;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;
import tgb.btc.rce.service.handler.message.text.ITextMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SlashCommandHandler implements ITextMessageHandler {

    private final IReadUserService readUserService;

    private final ISlashCommandService slashCommandService;

    private final Map<SlashCommand, ISlashCommandHandler> handlers = new HashMap<>();

    public SlashCommandHandler(List<ISlashCommandHandler> slashCommandHandlers, IReadUserService readUserService, ISlashCommandService slashCommandService) {
        this.readUserService = readUserService;
        this.slashCommandService = slashCommandService;
        for (ISlashCommandHandler handler : slashCommandHandlers) {
            handlers.put(handler.getSlashCommand(), handler);
        }
    }

    public boolean handle(Message message) {
        SlashCommand slashCommand = slashCommandService.fromMessageText(message.getText());
        if (Objects.nonNull(slashCommand) && slashCommand.hasAccess(readUserService.getUserRoleByChatId(message.getChatId()))) {
            ISlashCommandHandler slashCommandHandler = handlers.get(slashCommand);
            if (Objects.nonNull(slashCommandHandler)) {
                slashCommandHandler.handle(message);
                return true;
            }
        }
        return false;
    }

    @Override
    public TextMessageType getTextMessageType() {
        return TextMessageType.SLASH_COMMAND;
    }
}
