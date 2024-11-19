package tgb.btc.rce.service.util;

import tgb.btc.rce.enums.update.CallbackQueryData;

public interface ICallbackDataService {
    String buildData(CallbackQueryData data, Object... arguments);

    CallbackQueryData fromData(String data);

    String getArgument(String data, int index);

    Long getLongArgument(String data, int index);
}
