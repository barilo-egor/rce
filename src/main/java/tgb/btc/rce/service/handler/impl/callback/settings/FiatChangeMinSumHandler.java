package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class FiatChangeMinSumHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    public FiatChangeMinSumHandler(ICallbackDataService callbackDataService,
                                   IResponseSender responseSender, IKeyboardService keyboardService) {
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.sendEditedMessageText(chatId, callbackQuery.getMessage().getMessageId(),
                "Выберите тип сделки для изменения минимальной суммы.",
                keyboardService.getInlineDealTypes(CallbackQueryData.DEAL_TYPE_CHANGE_MIN_SUM, fiatCurrency));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.FIAT_CHANGE_MIN_SUM;
    }
}
