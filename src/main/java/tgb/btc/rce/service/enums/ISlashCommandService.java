package tgb.btc.rce.service.enums;

import tgb.btc.rce.enums.update.SlashCommand;

public interface ISlashCommandService {

    SlashCommand fromMessageText(String text);

    SlashCommand fromText(String text);
}
