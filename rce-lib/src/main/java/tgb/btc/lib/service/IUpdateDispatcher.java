package tgb.btc.lib.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.enums.Command;

public interface IUpdateDispatcher {
    void dispatch(Update update);

    void runProcessor(Command command, Long chatId, Update update);
}
