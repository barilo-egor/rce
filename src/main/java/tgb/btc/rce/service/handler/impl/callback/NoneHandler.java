package tgb.btc.rce.service.handler.impl.callback;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;

@Service
public class NoneHandler implements ICallbackQueryHandler {
    @Override
    public void handle(CallbackQuery callbackQuery) {
        // stub
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.NONE;
    }
}
