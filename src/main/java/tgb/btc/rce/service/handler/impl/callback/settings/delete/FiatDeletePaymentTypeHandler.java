package tgb.btc.rce.service.handler.impl.callback.settings.delete;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class FiatDeletePaymentTypeHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IFiatCurrencyService fiatCurrencyService;

    private final IKeyboardService keyboardService;

    public FiatDeletePaymentTypeHandler(ICallbackDataService callbackDataService, IResponseSender responseSender,
                                        IFiatCurrencyService fiatCurrencyService, IKeyboardService keyboardService) {
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.fiatCurrencyService = fiatCurrencyService;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        FiatCurrency fiatCurrency;
        if (fiatCurrencyService.isFew()) {
            fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        } else {
            fiatCurrency = fiatCurrencyService.getFirst();
        }
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.sendEditedMessageText(chatId, callbackQuery.getMessage().getMessageId(),
                "Выберите тип сделки для <b>" + fiatCurrency.getDisplayName() + "</b>.",
                keyboardService.getInlineDealTypes(CallbackQueryData.DEAL_TYPE_DELETE_PAYMENT_TYPE, fiatCurrency));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.FIAT_DELETE_PAYMENT_TYPE;
    }
}
