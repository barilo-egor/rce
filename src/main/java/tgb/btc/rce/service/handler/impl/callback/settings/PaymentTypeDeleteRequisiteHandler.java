package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.IShowRequisitesService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class PaymentTypeDeleteRequisiteHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IShowRequisitesService showRequisitesService;

    public PaymentTypeDeleteRequisiteHandler(ICallbackDataService callbackDataService,
                                             IShowRequisitesService showRequisitesService) {
        this.callbackDataService = callbackDataService;
        this.showRequisitesService = showRequisitesService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        showRequisitesService.showForDelete(chatId, paymentTypePid, callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PAYMENT_TYPE_DELETE_REQUISITE;
    }
}
