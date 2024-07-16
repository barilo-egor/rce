package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.Command;

public interface ICallbackQueryService {

    Integer messageId(Update update);

    String getSplitData(Update update, int index);

    String getSplitData(CallbackQuery callbackQuery, int index);

    Long getSplitLongData(Update update, int index);

    Boolean getSplitBooleanData(Update update, int index);

    <T> String buildCallbackData(Command command, T[] variables);

    boolean isBack(Update update);
}
