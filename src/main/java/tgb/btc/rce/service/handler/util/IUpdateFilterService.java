package tgb.btc.rce.service.handler.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.UpdateFilterType;

public interface IUpdateFilterService {

    UpdateFilterType getType(Update update);
}
