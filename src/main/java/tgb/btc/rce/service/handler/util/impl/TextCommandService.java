package tgb.btc.rce.service.handler.util.impl;

import jakarta.annotation.PostConstruct;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.util.ITextCommandService;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Service
public class TextCommandService implements ITextCommandService {

    private static final String CACHE_NAME = "messageCommandService";

    private final ButtonsDesignPropertiesReader buttonsDesignPropertiesReader;

    private final Map<TextCommand, String> commandTextMap = new EnumMap<>(TextCommand.class);

    public TextCommandService(ButtonsDesignPropertiesReader buttonsDesignPropertiesReader) {
        this.buttonsDesignPropertiesReader = buttonsDesignPropertiesReader;
    }

    @PostConstruct
    private void init() {
        for (TextCommand textCommand : TextCommand.values()) {
            String value = buttonsDesignPropertiesReader.getString(textCommand.name(), null);
            if (Objects.nonNull(value)) {
                commandTextMap.put(textCommand, value);
            } else {
                commandTextMap.put(textCommand, textCommand.getText());
            }
        }
    }

    @Cacheable(CACHE_NAME + "getText")
    @Override
    public String getText(TextCommand textCommand) {
        if (commandTextMap.containsKey(textCommand)) {
            return commandTextMap.get(textCommand);
        }
        return textCommand.getText();
    }

    @Cacheable(CACHE_NAME + "fromText")
    @Override
    public TextCommand fromText(String messageText) {
        for (Map.Entry<TextCommand, String> entry : commandTextMap.entrySet()) {
            if (Objects.equals(messageText, entry.getValue())) {
                return entry.getKey();
            }
        }
        for (TextCommand textCommand : TextCommand.values()) {
            if (textCommand.getText().equals(messageText)) {
                return textCommand;
            }
        }
        return null;
    }

    @Cacheable(CACHE_NAME + "isTextCommand")
    @Override
    public boolean isTextCommand(String messageText) {
        for (Map.Entry<TextCommand, String> entry : commandTextMap.entrySet()) {
            if (Objects.equals(messageText, entry.getValue())) {
                return true;
            }
        }
        for (TextCommand textCommand : TextCommand.values()) {
            if (textCommand.getText().equals(messageText)) {
                return true;
            }
        }
        return false;
    }
}
