package tgb.btc.rce.service.handler.message.text;

import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;

public interface ITextCommandHandler {

    void handle(Message message);

    TextCommand getTextCommand();
}
