package tgb.btc.rce.service.enums.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.service.enums.ISlashCommandService;

@Service
public class SlashCommandService implements ISlashCommandService {

    public static final String CACHE_NAME = "slashCommandService";

    @Cacheable(CACHE_NAME + "fromMessageText")
    @Override
    public SlashCommand fromMessageText(String text) {
        int spaceIndex = text.indexOf(" ");
        try {
            if (spaceIndex == -1) {
                return fromText(text);
            } else {
                return fromText(text.substring(0, spaceIndex));
            }
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Cacheable(CACHE_NAME + "fromText")
    @Override
    public SlashCommand fromText(String text) {
        for (SlashCommand slashCommand: SlashCommand.values()) {
            if (slashCommand.getText().equals(text)) {
                return slashCommand;
            }
        }
        return null;
    }
}
