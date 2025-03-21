package tgb.btc.rce.service.handler.impl.callback.settings.payment.discounts;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentTypeDiscountsHandler implements ITextCommandHandler {

    private final IPaymentTypeService paymentTypeService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    public PaymentTypeDiscountsHandler(IPaymentTypeService paymentTypeService, ICallbackDataService callbackDataService,
                                       IResponseSender responseSender) {
        this.paymentTypeService = paymentTypeService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(Message message) {
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndFiatCurrency(DealType.BUY, FiatCurrency.RUB);
        List<InlineButton> buttons = new ArrayList<>();
        paymentTypes.forEach(paymentType -> buttons.add(
                InlineButton.builder()
                        .text(paymentType.getName())
                        .data(callbackDataService.buildData(CallbackQueryData.PAYMENT_TYPE_DISCOUNTS, paymentType.getPid()))
                        .build()
        ));
        buttons.add(InlineButton.builder().text("Закрыть").data(CallbackQueryData.INLINE_DELETE.name()).build());
        responseSender.sendMessage(message.getChatId(), "Выберите тип оплаты, в котором хотите изменить скидки.", buttons);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.PAYMENT_TYPE_DISCOUNTS;
    }
}
