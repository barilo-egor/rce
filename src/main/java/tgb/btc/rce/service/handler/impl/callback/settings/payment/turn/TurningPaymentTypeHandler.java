package tgb.btc.rce.service.handler.impl.callback.settings.payment.turn;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.IShowPaymentTypesService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class TurningPaymentTypeHandler implements ICallbackQueryHandler {

    private final IPaymentTypeService paymentTypeService;

    private final ICallbackDataService callbackDataService;

    private final IPaymentRequisiteService paymentRequisiteService;

    private final IShowPaymentTypesService showPaymentTypesService;

    public TurningPaymentTypeHandler(IPaymentTypeService paymentTypeService,
                                     ICallbackDataService callbackDataService,
                                     IPaymentRequisiteService paymentRequisiteService,
                                     IShowPaymentTypesService showPaymentTypesService) {
        this.paymentTypeService = paymentTypeService;
        this.callbackDataService = callbackDataService;
        this.paymentRequisiteService = paymentRequisiteService;
        this.showPaymentTypesService = showPaymentTypesService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        paymentTypeService.updateIsOnByPid(callbackDataService.getBoolArgument(callbackQuery.getData(), 2), paymentTypePid);
        paymentRequisiteService.removeOrder(paymentTypePid);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        showPaymentTypesService.sendForTurn(chatId, paymentType.getDealType(), paymentType.getFiatCurrency(),
                callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.TURNING_PAYMENT_TYPES;
    }
}
