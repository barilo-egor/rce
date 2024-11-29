package tgb.btc.rce.service.handler;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;

public interface ICallbackQueryHandler {

    void handle(CallbackQuery callbackQuery);

    CallbackQueryData getCallbackQueryData();
}
