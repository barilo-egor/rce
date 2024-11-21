package tgb.btc.rce.service.handler.impl.callback.util;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;

@Service
public class InlineDeleteHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    public InlineDeleteHandler(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        responseSender.deleteMessage(callbackQuery.getFrom().getId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.INLINE_DELETE;
    }
}
