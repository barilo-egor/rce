package tgb.btc.rce.service.handler.impl.callback.util;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Set;

@Service
public class InlineDeleteHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public InlineDeleteHandler(IResponseSender responseSender, ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        if (callbackDataService.hasArguments(callbackQuery.getData())) {
            Set<Integer> arguments = callbackDataService.getIntArguments(callbackQuery.getData());
            arguments.forEach(messageId -> responseSender.deleteMessage(chatId, messageId));
        }
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.INLINE_DELETE;
    }
}
