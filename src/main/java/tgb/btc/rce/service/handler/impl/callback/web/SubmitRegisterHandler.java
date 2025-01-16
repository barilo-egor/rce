package tgb.btc.rce.service.handler.impl.callback.web;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.api.bot.WebAPI;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class SubmitRegisterHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final WebAPI webAPI;

    public SubmitRegisterHandler(IResponseSender responseSender,
                                 WebAPI webAPI) {
        this.responseSender = responseSender;
        this.webAPI = webAPI;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        webAPI.submitChatId(chatId);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SUBMIT_REGISTER;
    }
}
