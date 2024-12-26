package tgb.btc.rce.service.handler.impl.callback.web;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.api.bot.WebAPI;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

@Service
public class SubmitLoginHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final WebAPI webAPI;

    public SubmitLoginHandler(ICallbackDataService callbackDataService, IResponseSender responseSender, WebAPI webAPI) {
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.webAPI = webAPI;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        webAPI.submitLogin(chatId);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Вы можете, если потребуется, закрыть сессию по кнопке ниже.",
                InlineButton.builder()
                        .text("Закрыть сессию")
                        .data(CallbackQueryData.LOGOUT.name())
                        .build());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SUBMIT_LOGIN;
    }
}
