package tgb.btc.rce.service.handler.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.service.handler.IUpdateHandler;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CallbackQueryUpdateHandler implements IUpdateHandler {

    private final ICallbackDataService callbackDataService;

    private final Map<CallbackQueryData, ICallbackQueryHandler> callbackQueryHandlerMap = new HashMap<>();

    public CallbackQueryUpdateHandler(List<ICallbackQueryHandler> callbackQueryHandlers, ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
        for (ICallbackQueryHandler callbackQueryHandler : callbackQueryHandlers) {
            callbackQueryHandlerMap.put(callbackQueryHandler.getCallbackQueryData(), callbackQueryHandler);
        }
    }

    @Override
    public boolean handle(Update update) {
        CallbackQueryData callbackQueryData = callbackDataService.fromData(update.getCallbackQuery().getData());
        if (Objects.isNull(callbackQueryData)) return false;
        ICallbackQueryHandler callbackQueryHandler = callbackQueryHandlerMap.get(callbackQueryData);
        if (Objects.isNull(callbackQueryHandler)) return false;
        callbackQueryHandler.handle(update.getCallbackQuery());
        return true;
    }


    @Override
    public UpdateType getUpdateType() {
        return UpdateType.CALLBACK_QUERY;
    }
}
