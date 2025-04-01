package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.web.merchant.onlypays.OnlyPaysPaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Objects;

@Service
public class OnlyPaysMethodHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final IResponseSender responseSender;

    public OnlyPaysMethodHandler(ICallbackDataService callbackDataService, IPaymentTypeService paymentTypeService,
                                 IResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        Long paymentTypePid = callbackDataService.getLongArgument(data, 1);
        String paymentTypeName = callbackDataService.getArgument(data, 2);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        if (Objects.nonNull(paymentTypeName)) {
            OnlyPaysPaymentType onlyPaysPaymentType = OnlyPaysPaymentType.valueOf(paymentTypeName);
            paymentType.setOnlyPaysPaymentType(onlyPaysPaymentType);
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> связан с OnlyPays методом оплаты <b>\"" + onlyPaysPaymentType.getDescription()
                    + "\"</b>.");
        } else {
            paymentType.setOnlyPaysPaymentType(null);
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> отвязан от OnlyPays метода оплаты.");
        }
        paymentTypeService.save(paymentType);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ONLY_PAYS_METHOD;
    }
}
