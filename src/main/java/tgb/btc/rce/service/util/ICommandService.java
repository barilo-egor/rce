package tgb.btc.rce.service.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;

public interface ICommandService {


    boolean isStartCommand(Update update);

    boolean isSubmitCommand(Update update);

    String getText(Command command);

    Command fromUpdate(Update update);

    Command fromCallbackQuery(String value);

    Command findByTextOrName(String value);
}
