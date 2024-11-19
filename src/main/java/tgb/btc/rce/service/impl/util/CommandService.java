package tgb.btc.rce.service.impl.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.library.service.properties.DiceProperties;
import tgb.btc.library.service.properties.RPSPropertiesReader;
import tgb.btc.library.service.properties.SlotReelPropertiesReader;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.util.ICommandService;

import java.util.*;

import static tgb.btc.rce.enums.Command.*;

@Service
public class CommandService implements ICommandService {

    private final IUpdateService updateService;

    private final Set<Command> BUTTONS_DESIGN_COMMANDS = Set.of(
            BACK, BUY_BITCOIN, SELL_BITCOIN, CABINET, CONTACTS, DRAWS, REFERRAL, LOTTERY, ROULETTE
    );

    private final Set<Command> SLOT_REEL_COMMANDS = Set.of(SLOT_REEL);

    private final Set<Command> DICE_COMMANDS = Set.of(DICE);

    private final Set<Command> RPS_COMMANDS = Set.of(RPS);

    private ButtonsDesignPropertiesReader buttonsDesignPropertiesReader;

    private SlotReelPropertiesReader slotReelPropertiesReader;

    private DiceProperties diceProperties;

    private RPSPropertiesReader rpsPropertiesReader;

    @Autowired
    public void setButtonsDesignPropertiesReader(ButtonsDesignPropertiesReader buttonsDesignPropertiesReader) {
        this.buttonsDesignPropertiesReader = buttonsDesignPropertiesReader;
    }

    @Autowired
    public void setSlotReelPropertiesReader(SlotReelPropertiesReader slotReelPropertiesReader) {
        this.slotReelPropertiesReader = slotReelPropertiesReader;
    }

    @Autowired
    public void setDiceProperties(DiceProperties diceProperties) {
        this.diceProperties = diceProperties;
    }

    @Autowired
    public void setRpsPropertiesReader(RPSPropertiesReader rpsPropertiesReader) {
        this.rpsPropertiesReader = rpsPropertiesReader;
    }

    @Autowired
    public CommandService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Override
    public boolean isStartCommand(Update update) {
        return updateService.hasMessageText(update) && update.getMessage().getText().startsWith(START.getText());
    }

    @Override
    public boolean isSubmitCommand(Update update) {
        return update.hasCallbackQuery() &&
                (update.getCallbackQuery().getData().startsWith(SUBMIT_LOGIN.name())
                        || update.getCallbackQuery().getData().startsWith(SUBMIT_REGISTER.name()));
    }

    @Override
    @Cacheable(value = "commandTextCache")
    public String getText(Command command) {
        if (BUTTONS_DESIGN_COMMANDS.contains(command)) {
            return buttonsDesignPropertiesReader.getString(command.name());
        }
        if (SLOT_REEL_COMMANDS.contains(command)) {
            return slotReelPropertiesReader.getString(command.name());
        }
        if (DICE_COMMANDS.contains(command)) {
            return diceProperties.getString(command.name());
        }
        if (RPS_COMMANDS.contains(command)) {
            return rpsPropertiesReader.getString(command.name());
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
                return CHANNEL_POST;
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
