package tgb.btc.rce.service.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface IUpdateDispatcher {
    void dispatch(Update update);
}
