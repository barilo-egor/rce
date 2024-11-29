package tgb.btc.rce.service.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.TextCommand;

public interface ICommandService {

    String getText(TextCommand command);

    Command fromUpdate(Update update);

}
