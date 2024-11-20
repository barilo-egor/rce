package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.IShowPaymentTypesService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class TurningPaymentTypeHandler implements ICallbackQueryHandler {

    private final IPaymentTypeService paymentTypeService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IPaymentRequisiteService paymentRequisiteService;

    private final IShowPaymentTypesService showPaymentTypesService;

    public TurningPaymentTypeHandler(IPaymentTypeService paymentTypeService, IResponseSender responseSender,
                                     ICallbackDataService callbackDataService,
                                     IPaymentRequisiteService paymentRequisiteService,
                                     IShowPaymentTypesService showPaymentTypesService) {
        this.paymentTypeService = paymentTypeService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.paymentRequisiteService = paymentRequisiteService;
        this.showPaymentTypesService = showPaymentTypesService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        paymentTypeService.updateIsOnByPid(Boolean.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 2)), paymentTypePid);
        paymentRequisiteService.removeOrder(paymentTypePid);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        showPaymentTypesService.sendForTurn(chatId, paymentType.getDealType(), paymentType.getFiatCurrency());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.TURNING_PAYMENT_TYPES;
    }
}
