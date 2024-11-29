package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.util.ICommandService;

import java.util.Set;

@Service
public class CommandService implements ICommandService {

    private final Set<TextCommand> BUTTONS_DESIGN_TEXT_COMMANDS = Set.of(
            TextCommand.BACK, TextCommand.BUY_BITCOIN, TextCommand.SELL_BITCOIN, TextCommand.CONTACTS, TextCommand.DRAWS,
            TextCommand.REFERRAL, TextCommand.LOTTERY, TextCommand.ROULETTE
    );

    private final ButtonsDesignPropertiesReader buttonsDesignPropertiesReader;

    @Autowired
    public CommandService(ButtonsDesignPropertiesReader buttonsDesignPropertiesReader) {
        this.buttonsDesignPropertiesReader = buttonsDesignPropertiesReader;
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
        return null;
    }

}
