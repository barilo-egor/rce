package tgb.btc.rce.service.util;

import org.springframework.cache.annotation.Cacheable;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.TextCommand;

public interface ICommandService {


    boolean isStartCommand(Update update);

    boolean isSubmitCommand(Update update);

    String getText(Command command);

    @Cacheable(value = "textCommandTextCache")
    String getText(TextCommand command);

    Command fromUpdate(Update update);

    Command fromCallbackQuery(String value);

    Command findByTextOrName(String value);
}
