package tgb.btc.rce.service.handler.impl.callback.settings.payment.discounts;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentTypeDiscount;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeDiscountService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class DeletePaymentDiscountHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeDiscountService paymentTypeDiscountService;

    private final PaymentTypeDiscountsHandler paymentTypeDiscountsHandler;

    private final IResponseSender responseSender;

    public DeletePaymentDiscountHandler(ICallbackDataService callbackDataService,
                                        IPaymentTypeDiscountService paymentTypeDiscountService,
                                        PaymentTypeDiscountsHandler paymentTypeDiscountsHandler,
                                        IResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.paymentTypeDiscountService = paymentTypeDiscountService;
        this.paymentTypeDiscountsHandler = paymentTypeDiscountsHandler;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Long paymentTypeDiscountPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        PaymentTypeDiscount paymentTypeDiscount = paymentTypeDiscountService.getByPid(paymentTypeDiscountPid);
        Long paymentTypePid = paymentTypeDiscount.getPaymentType().getPid();
        String paymentTypeName = paymentTypeDiscount.getPaymentType().getName();
        Double percent = paymentTypeDiscount.getPercent();
        Integer maxAmount = paymentTypeDiscount.getMaxAmount();
        paymentTypeDiscountService.delete(paymentTypeDiscount);
        responseSender.sendMessage(chatId, "Скидка <b>" + percent + "% до " + maxAmount
                + FiatCurrency.RUB.getGenitive() + "</b> была удалена из типа оплаты <b>\"" + paymentTypeName + "\"</b>.");
        paymentTypeDiscountsHandler.sendPaymentTypeDiscounts(chatId, paymentTypePid);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DELETE_PAYMENT_TYPE_DISCOUNT;
    }
}
