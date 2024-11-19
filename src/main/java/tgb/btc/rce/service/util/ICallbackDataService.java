package tgb.btc.rce.service.util;

import tgb.btc.rce.enums.update.CallbackQueryData;

public interface ICallbackDataService {
    CallbackQueryData fromData(String data);
}
