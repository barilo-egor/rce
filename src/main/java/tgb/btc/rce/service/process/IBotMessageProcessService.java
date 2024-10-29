package tgb.btc.rce.service.process;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IBotMessageProcessService {

    void askForType(Long chatId);

    void askForNewValue(Update update);

    void updateValue(Update update);
}
