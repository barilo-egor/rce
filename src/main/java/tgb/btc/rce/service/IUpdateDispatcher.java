package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;

public interface IUpdateDispatcher {
    void dispatch(Update update);

    void runProcessor(Command command, Long chatId, Update update);

    boolean isOn();
}
