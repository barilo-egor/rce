package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.processors.admin.settings.paymenttypes.delete.ShowPaymentTypesForDelete;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class DeletingPaymentTypeHandler implements ICallbackQueryHandler {

    private final IPaymentTypeService paymentTypeService;

    private final IPaymentRequisiteService paymentRequisiteService;

    private final ShowPaymentTypesForDelete showPaymentTypesForDelete;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public DeletingPaymentTypeHandler(IPaymentTypeService paymentTypeService, IPaymentRequisiteService paymentRequisiteService,
                                      ShowPaymentTypesForDelete showPaymentTypesForDelete, IResponseSender responseSender,
                                      ICallbackDataService callbackDataService) {
        this.paymentTypeService = paymentTypeService;
        this.paymentRequisiteService = paymentRequisiteService;
        this.showPaymentTypesForDelete = showPaymentTypesForDelete;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long pid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        PaymentType paymentType = paymentTypeService.getByPid(pid);
        DealType dealType = paymentType.getDealType();
        paymentRequisiteService.deleteByPaymentTypePid(paymentType.getPid());
        paymentRequisiteService.removeOrder(paymentType.getPid());
        paymentTypeService.deleteById(pid);
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        String message = "Тип оплаты на " + paymentType.getDealType().getAccusative() + " \"" + paymentType.getName() + "\" удален.";
        responseSender.sendMessage(callbackQuery.getFrom().getId(), message);
        showPaymentTypesForDelete.sendPaymentTypes(chatId, dealType, paymentType.getFiatCurrency());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETING_PAYMENT_TYPE;
    }
}
