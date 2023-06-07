package tgb.btc.lib.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateRunnable {

    void run(Update update);
}
