package tgb.btc.rce.service.handler.impl.callback.settings.payment.turn;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.IShowPaymentTypesService;
import tgb.btc.rce.service.impl.util.CallbackDataService;

@Service
public class DealTypeTurnPaymentTypesHandler implements ICallbackQueryHandler {

    private final CallbackDataService callbackDataService;

    private final IShowPaymentTypesService showPaymentTypesService;

    public DealTypeTurnPaymentTypesHandler(CallbackDataService callbackDataService, IShowPaymentTypesService showPaymentTypesService) {
        this.callbackDataService = callbackDataService;
        this.showPaymentTypesService = showPaymentTypesService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        DealType dealType = DealType.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 2));
        showPaymentTypesService.sendForTurn(chatId, dealType, fiatCurrency, callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DEAL_TYPE_TURN_PAYMENT_TYPES;
    }
}
