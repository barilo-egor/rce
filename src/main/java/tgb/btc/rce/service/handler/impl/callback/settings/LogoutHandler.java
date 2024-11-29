package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.api.bot.WebAPI;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;

@Service
public class LogoutHandler implements ICallbackQueryHandler {

    private final WebAPI webAPI;

    private final IResponseSender responseSender;

    public LogoutHandler(WebAPI webAPI, IResponseSender responseSender) {
        this.webAPI = webAPI;
        this.responseSender = responseSender;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        webAPI.logout(chatId);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Сессия закрыта.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.LOGOUT;
    }
}
