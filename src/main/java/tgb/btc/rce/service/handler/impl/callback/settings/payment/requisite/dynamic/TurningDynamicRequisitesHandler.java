package tgb.btc.rce.service.handler.impl.callback.settings.payment.requisite.dynamic;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.ITurnDynamicRequisiteService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.List;

@Service
public class TurningDynamicRequisitesHandler implements ICallbackQueryHandler {

    private final IPaymentTypeService paymentTypeService;

    private final IPaymentRequisiteService paymentRequisiteService;

    private final ITurnDynamicRequisiteService turnDynamicRequisiteService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public TurningDynamicRequisitesHandler(IPaymentTypeService paymentTypeService,
                                           IPaymentRequisiteService paymentRequisiteService,
                                           ITurnDynamicRequisiteService turnDynamicRequisiteService,
                                           IResponseSender responseSender,
                                           ICallbackDataService callbackDataService) {
        this.paymentTypeService = paymentTypeService;
        this.paymentRequisiteService = paymentRequisiteService;
        this.turnDynamicRequisiteService = turnDynamicRequisiteService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long pid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        boolean value = Boolean.parseBoolean(callbackDataService.getArgument(callbackQuery.getData(), 2));
        PaymentType paymentType = paymentTypeService.getByPid(pid);
        if (!value) {
            paymentTypeService.updateIsDynamicOnByPid(false, pid);
            turnDynamicRequisiteService.sendPaymentTypes(chatId, callbackQuery.getMessage().getMessageId(), paymentType.getFiatCurrency());
        } else {
            List<PaymentRequisite> paymentRequisites = paymentRequisiteService.getByPaymentType_Pid(pid);
            if (paymentRequisites.size() <= 1) {
                responseSender.sendMessage(chatId, "Недостаточно реквизитов для включения. Количество реквизитов: "
                        + paymentRequisites.size() + ".");
                return;
            }
            paymentTypeService.updateIsDynamicOnByPid(true, pid);
            turnDynamicRequisiteService.sendPaymentTypes(chatId, callbackQuery.getMessage().getMessageId(), paymentType.getFiatCurrency());
        }
        paymentRequisiteService.removeOrder(paymentType.getPid());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.TURNING_DYNAMIC_REQUISITES;
    }
}
