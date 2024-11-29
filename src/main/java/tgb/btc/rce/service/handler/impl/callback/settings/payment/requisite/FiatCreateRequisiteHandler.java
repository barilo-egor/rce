package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.impl.message.text.command.settings.NewPaymentTypeRequisiteHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class FiatCreateRequisiteHandler implements ICallbackQueryHandler {

    private final NewPaymentTypeRequisiteHandler newPaymentTypeRequisiteHandler;

    private final ICallbackDataService callbackDataService;

    public FiatCreateRequisiteHandler(NewPaymentTypeRequisiteHandler newPaymentTypeRequisiteHandler,
                                      ICallbackDataService callbackDataService) {
        this.newPaymentTypeRequisiteHandler = newPaymentTypeRequisiteHandler;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        newPaymentTypeRequisiteHandler.sendPaymentTypes(callbackQuery.getFrom().getId(), fiatCurrency, callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.FIAT_CREATE_REQUISITE;
    }
}
