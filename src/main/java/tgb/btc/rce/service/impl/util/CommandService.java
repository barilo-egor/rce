package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.util.ICommandService;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import static tgb.btc.rce.enums.Command.*;

@Service
public class CommandService implements ICommandService {

    private final IUpdateService updateService;

    private final Set<Command> BUTTONS_DESIGN_COMMANDS = Set.of(
            BACK, BUY_BITCOIN, SELL_BITCOIN, CONTACTS, DRAWS, REFERRAL, LOTTERY, ROULETTE
    );

    private final Set<TextCommand> BUTTONS_DESIGN_TEXT_COMMANDS = Set.of(
            TextCommand.BACK, TextCommand.BUY_BITCOIN, TextCommand.SELL_BITCOIN, TextCommand.CONTACTS, TextCommand.DRAWS,
            TextCommand.REFERRAL, TextCommand.LOTTERY, TextCommand.ROULETTE
    );

    private final ButtonsDesignPropertiesReader buttonsDesignPropertiesReader;

    @Autowired
    public CommandService(IUpdateService updateService, ButtonsDesignPropertiesReader buttonsDesignPropertiesReader) {
        this.updateService = updateService;
        this.buttonsDesignPropertiesReader = buttonsDesignPropertiesReader;
    }

    @Override
    public boolean isStartCommand(Update update) {
        return updateService.hasMessageText(update) && update.getMessage().getText().startsWith(SlashCommand.START.getText());
    }

    @Override
    public boolean isSubmitCommand(Update update) {
        return update.hasCallbackQuery() &&
                (update.getCallbackQuery().getData().startsWith(CallbackQueryData.SUBMIT_LOGIN.name())
                        || update.getCallbackQuery().getData().startsWith(CallbackQueryData.SUBMIT_REGISTER.name()));
    }

    @Override
    public String getText(Command command) {
        if (BUTTONS_DESIGN_COMMANDS.contains(command)) {
            return buttonsDesignPropertiesReader.getString(command.name());
        }
        return command.getText();
    }

    @Override
    public String getText(TextCommand command) {
        if (BUTTONS_DESIGN_TEXT_COMMANDS.contains(command)) {
            return buttonsDesignPropertiesReader.getString(command.name());
        }
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
                return null;
            case EDITED_CHANNEL_POST:
            case MY_CHAT_MEMBER:
                // TODO переделать логику, чтобы регистрировало по myChatMember а не по команде /start
                return NONE;
            default:
                return START;
        }
    }

    @Override
    public Command fromCallbackQuery(String value) {
        if (value.contains(BotStringConstants.CALLBACK_DATA_SPLITTER)) {
            value = value.split(BotStringConstants.CALLBACK_DATA_SPLITTER)[0];
        }
        try {
            return valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public Command findByTextOrName(String value) {
        return Arrays.stream(values())
                .filter(command -> (Objects.nonNull(getText(command))
                        && (value.equals(getText(command))) || (command.isHidden() && value.startsWith(command.getText()))))
                .findFirst()
                .orElse(null);
    }
}
