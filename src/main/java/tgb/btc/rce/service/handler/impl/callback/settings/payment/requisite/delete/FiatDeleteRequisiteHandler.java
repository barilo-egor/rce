package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.delete;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.impl.message.text.command.settings.payment.DeleteRequisiteHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class FiatDeleteRequisiteHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final DeleteRequisiteHandler deleteRequisiteHandler;

    public FiatDeleteRequisiteHandler(ICallbackDataService callbackDataService,
                                      DeleteRequisiteHandler deleteRequisiteHandler) {
        this.callbackDataService = callbackDataService;
        this.deleteRequisiteHandler = deleteRequisiteHandler;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        deleteRequisiteHandler.sendPaymentTypes(callbackQuery.getFrom().getId(), fiatCurrency, callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.FIAT_DELETE_REQUISITE;
    }
}
