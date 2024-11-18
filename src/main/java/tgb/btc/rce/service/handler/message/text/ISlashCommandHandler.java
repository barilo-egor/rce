package tgb.btc.rce.service.handler.message.text;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface ISlashCommandHandler {

    void handle(Message message);

    String getSlashCommand();
}
