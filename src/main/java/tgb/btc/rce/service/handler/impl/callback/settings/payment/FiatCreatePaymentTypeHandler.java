package tgb.btc.rce.service.handler.impl.callback.settings.payment;

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
public class FiatCreatePaymentTypeHandler implements ICallbackQueryHandler {

    private final IFiatCurrencyService fiatCurrencyService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    public FiatCreatePaymentTypeHandler(IFiatCurrencyService fiatCurrencyService,
                                        ICallbackDataService callbackDataService,
                                        IResponseSender responseSender, IKeyboardService keyboardService) {
        this.fiatCurrencyService = fiatCurrencyService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
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
                "Выберите тип сделки для нового <b>" + fiatCurrency.getDisplayName() + "</b> типа оплаты.",
                keyboardService.getInlineDealTypes(CallbackQueryData.DEAL_TYPE_CREATE_PAYMENT_TYPE, fiatCurrency));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.FIAT_CREATE_PAYMENT_TYPE;
    }
}
