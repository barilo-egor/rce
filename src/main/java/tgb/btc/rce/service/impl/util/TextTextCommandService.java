package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.util.ITextCommandService;

import java.util.Set;

@Service
public class TextTextCommandService implements ITextCommandService {

    private final Set<TextCommand> buttonsDesignTextCommands = Set.of(
            TextCommand.BACK, TextCommand.BUY_BITCOIN, TextCommand.SELL_BITCOIN, TextCommand.CONTACTS, TextCommand.DRAWS,
            TextCommand.REFERRAL, TextCommand.LOTTERY, TextCommand.ROULETTE
    );

    private final ButtonsDesignPropertiesReader buttonsDesignPropertiesReader;

    @Autowired
    public TextTextCommandService(ButtonsDesignPropertiesReader buttonsDesignPropertiesReader) {
        this.buttonsDesignPropertiesReader = buttonsDesignPropertiesReader;
    }

    @Override
    public String getText(TextCommand command) {
        if (buttonsDesignTextCommands.contains(command)) {
            return buttonsDesignPropertiesReader.getString(command.name());
        }
        return command.getText();
    }

}
