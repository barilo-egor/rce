package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.UpdateType;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.util.ICommandService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommandService implements ICommandService {

    private final Map<Command, String> propertiesCommandsText = new EnumMap<>(Command.class);

    private final IUpdateService updateService;

    @PostConstruct
    private void init() {
        Set<Command> propertiesCommands = Arrays.stream(Command.values())
                .filter(command ->
                        Arrays.stream(PropertiesPath.values())
                                .anyMatch(propertiesPath -> propertiesPath.name().equals(command.getText())))
                .collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
        for (Command command : Command.values()) {
            if (propertiesCommands.contains(command))
                propertiesCommandsText.put(command, PropertiesPath.valueOf(command.getText()).getString(command.name()));
        }
    }

    @Autowired
    public CommandService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Override
    public boolean isStartCommand(Update update) {
        return updateService.hasMessageText(update) && Command.START.equals(fromUpdate(update));
    }

    @Override
    public boolean isSubmitCommand(Update update) {
        return update.hasCallbackQuery() &&
                (update.getCallbackQuery().getData().startsWith(Command.SUBMIT_LOGIN.name())
                        || update.getCallbackQuery().getData().startsWith(Command.SUBMIT_REGISTER.name()));
    }

    @Override
    @Cacheable(value = "commandTextCache")
    public String getText(Command command) {
        if (propertiesCommandsText.containsKey(command))
            return propertiesCommandsText.get(command);
        return command.getText();
    }

    @Override
    public Command fromUpdate(Update update) {
        switch (UpdateType.fromUpdate(update)) {
            case MESSAGE:
                return findByTextOrName(update.getMessage().getText());
            case CALLBACK_QUERY:
                return fromCallbackQuery(update.getCallbackQuery().getData());
            case INLINE_QUERY:
                return findByTextOrName(update.getInlineQuery().getQuery());
            case CHANNEL_POST:
                return Command.CHANNEL_POST;
            default:
                return Command.START;
        }
    }

    @Override
    public Command fromCallbackQuery(String value) {
        if (value.contains(BotStringConstants.CALLBACK_DATA_SPLITTER)) {
            value = value.split(BotStringConstants.CALLBACK_DATA_SPLITTER)[0];
        }
        try {
            return Command.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Command findByTextOrName(String value) {
        return Arrays.stream(Command.values())
                .filter(command -> (Objects.nonNull(getText(command)) && value.startsWith(getText(command))) || value.startsWith(command.name()))
                .findFirst()
                .orElse(null);
    }
}
