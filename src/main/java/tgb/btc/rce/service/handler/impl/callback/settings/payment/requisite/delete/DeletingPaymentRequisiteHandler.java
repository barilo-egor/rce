package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.delete;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.IShowRequisitesService;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class DeletingPaymentRequisiteHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IPaymentRequisiteService paymentRequisiteService;

    private final IShowRequisitesService showRequisitesService;

    public DeletingPaymentRequisiteHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                           IPaymentRequisiteService paymentRequisiteService,
                                           IShowRequisitesService showRequisitesService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.paymentRequisiteService = paymentRequisiteService;
        this.showRequisitesService = showRequisitesService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long pid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);

        PaymentRequisite paymentRequisite = paymentRequisiteService.findById(pid);
        String requisite = paymentRequisite.getRequisite();
        PaymentType paymentType = paymentRequisiteService.getPaymentTypeByPid(paymentRequisite.getPid());

        Long chatId = callbackQuery.getFrom().getId();
        paymentRequisiteService.delete(paymentRequisite);
        paymentRequisiteService.removeOrder(paymentType.getPid());
        responseSender.sendMessage(chatId, "Реквизит <b>" + requisite + "</b> удален.");
        showRequisitesService.showForDelete(callbackQuery.getFrom().getId(), paymentType.getPid(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETING_PAYMENT_TYPE_REQUISITE;
    }
}
