package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.impl.util.CallbackDataService;

@Service
public class SaveRequisiteHandler implements ICallbackQueryHandler {


    private final CallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final IPaymentRequisiteService paymentRequisiteService;

    private final ResponseSender responseSender;

    public SaveRequisiteHandler(CallbackDataService callbackDataService, IPaymentTypeService paymentTypeService,
                                IPaymentRequisiteService paymentRequisiteService, ResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.paymentRequisiteService = paymentRequisiteService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        String requisite = callbackDataService.getArgument(callbackQuery.getData(), 1);
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 2);
        PaymentRequisite paymentRequisite = new PaymentRequisite();
        paymentRequisite.setOn(true);
        paymentRequisite.setRequisite(requisite);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        paymentRequisite.setPaymentType(paymentType);
        paymentRequisiteService.save(paymentRequisite);
        paymentRequisiteService.removeOrder(paymentType.getPid());
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Реквизит <b>" + requisite + "</b> успешно сохранен.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SAVE_REQUISITE;
    }
}
