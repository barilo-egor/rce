package tgb.btc.rce.service.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UpdateFilterType;

public interface IUpdateFilter {

    void handle(Update update);

    UpdateFilterType getType();
}
