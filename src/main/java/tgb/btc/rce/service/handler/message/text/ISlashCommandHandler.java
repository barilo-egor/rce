package tgb.btc.rce.service.handler.message.text;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.SlashCommand;

public interface ISlashCommandHandler {

    void handle(Message message);

    SlashCommand getSlashCommand();
}
