package tgb.btc.rce.service.handler.impl.callback.settings.payment.create;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.math.BigDecimal;

@Service
public class SavePaymentTypeHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IPaymentTypeService paymentTypeService;

    public SavePaymentTypeHandler(ICallbackDataService callbackDataService, IResponseSender responseSender,
                                  IPaymentTypeService paymentTypeService) {
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.paymentTypeService = paymentTypeService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        String name = callbackDataService.getArgument(callbackQuery.getData(), 1);
        DealType dealType = DealType.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 2));
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 3));
        PaymentType paymentType = new PaymentType();
        paymentType.setName(name);
        paymentType.setDealType(dealType);
        paymentType.setMinSum(BigDecimal.ZERO);
        paymentType.setFiatCurrency(fiatCurrency);
        paymentTypeService.save(paymentType);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Новый тип оплаты <b>" + name + "</b> сохранен. " +
                "Не забудьте установить минимальную сумму, добавить реквизиты и включить по необходимости.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SAVE_PAYMENT_TYPE;
    }
}
