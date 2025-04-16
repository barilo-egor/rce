package tgb.btc.rce.service.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.update.CallbackQueryData;

import java.util.Set;

public interface ICallbackDataService {
    String buildData(CallbackQueryData data, Object... arguments);

    CallbackQueryData fromData(String data);

    String getArgument(String data, int index);

    Long getLongArgument(String data, int index);

    Integer getIntArgument(String data, int index);

    Set<Integer> getIntArguments(String data);

    Boolean getBoolArgument(String data, int index);

    boolean isCallbackQueryData(CallbackQueryData callbackQueryData, String data);

    boolean hasArguments(String data);

    boolean isBack(Update update);
}
