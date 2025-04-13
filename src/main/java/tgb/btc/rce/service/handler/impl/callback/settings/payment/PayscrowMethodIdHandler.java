package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.service.web.merchant.payscrow.PayscrowMerchantService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Objects;

@Service
public class PayscrowMethodIdHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final PayscrowMerchantService payscrowMerchantService;

    public PayscrowMethodIdHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                   IPaymentTypeService paymentTypeService, PayscrowMerchantService payscrowMerchantService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.payscrowMerchantService = payscrowMerchantService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        Long paymentTypePid = callbackDataService.getLongArgument(data, 1);
        String methodId = callbackDataService.getArgument(data, 2);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        paymentType.setPayscrowPaymentMethodId(methodId);
        paymentTypeService.save(paymentType);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        if (Objects.nonNull(methodId)) {
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> связан с Payscrow методом оплаты <b>\"" + payscrowMerchantService.getPaymentMethodName(methodId)
                    + "\"</b>.");
        } else {
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> отвязан от Payscrow метода оплаты.");
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PAYSCROW_METHOD;
    }
}
