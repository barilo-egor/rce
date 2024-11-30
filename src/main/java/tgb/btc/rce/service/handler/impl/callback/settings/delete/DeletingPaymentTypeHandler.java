package tgb.btc.rce.service.handler.impl.callback.settings.delete;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.impl.callback.settings.payment.delete.DealTypeDeletePaymentTypeHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class DeletingPaymentTypeHandler implements ICallbackQueryHandler {

    private final IPaymentTypeService paymentTypeService;

    private final IPaymentRequisiteService paymentRequisiteService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final DealTypeDeletePaymentTypeHandler dealTypeDeletePaymentTypeHandler;

    private final IModifyDealService modifyDealService;

    public DeletingPaymentTypeHandler(IPaymentTypeService paymentTypeService, IPaymentRequisiteService paymentRequisiteService,
                                      IResponseSender responseSender,
                                      ICallbackDataService callbackDataService,
                                      DealTypeDeletePaymentTypeHandler dealTypeDeletePaymentTypeHandler,
                                      IModifyDealService modifyDealService) {
        this.paymentTypeService = paymentTypeService;
        this.paymentRequisiteService = paymentRequisiteService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.dealTypeDeletePaymentTypeHandler = dealTypeDeletePaymentTypeHandler;
        this.modifyDealService = modifyDealService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long pid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        PaymentType paymentType = paymentTypeService.getByPid(pid);
        DealType dealType = paymentType.getDealType();
        modifyDealService.updatePaymentTypeToNullByPaymentTypePid(paymentType.getPid());
        paymentRequisiteService.deleteByPaymentTypePid(paymentType.getPid());
        paymentRequisiteService.removeOrder(paymentType.getPid());
        paymentTypeService.deleteById(pid);
        Long chatId = callbackQuery.getFrom().getId();
        String message = "Тип оплаты на " + paymentType.getDealType().getAccusative() + " \"" + paymentType.getName() + "\" удален.";
        responseSender.sendMessage(callbackQuery.getFrom().getId(), message);
        dealTypeDeletePaymentTypeHandler.sendPaymentTypes(chatId, callbackQuery.getMessage().getMessageId(), dealType,
                paymentType.getFiatCurrency());
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETING_PAYMENT_TYPE;
    }
}
