package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Processor {
    void run(Update update);
}
