package tgb.btc.rce.service.enums.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tgb.btc.rce.constants.BotCacheNames;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.service.enums.ISlashCommandService;

import java.util.Objects;

@Service
public class SlashCommandService implements ISlashCommandService {

    @Cacheable(BotCacheNames.SLASH_COMMAND_SERVICE + "fromMessageText")
    @Override
    public SlashCommand fromMessageText(String text) {
        if (Objects.isNull(text) || text.isEmpty()) {
            return null;
        }
        int spaceIndex = text.indexOf(" ");
        if (spaceIndex == -1) {
            return fromText(text);
        } else {
            return fromText(text.substring(0, spaceIndex));
        }
    }

    @Cacheable(BotCacheNames.SLASH_COMMAND_SERVICE + "fromText")
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
