package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

import java.util.List;

@Service
public class AdditionalPaymentTypeText implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IPaymentTypeService paymentTypeService;

    public AdditionalPaymentTypeText(IResponseSender responseSender, IPaymentTypeService paymentTypeService) {
        this.responseSender = responseSender;
        this.paymentTypeService = paymentTypeService;
    }

    @Override
    public void handle(Message message) {
        List<PaymentType> paymentTypeList = paymentTypeService.getByDealType(DealType.BUY);
        responseSender.sendMessage(message.getChatId(), "Типы оплаты на <b>" + DealType.BUY.getGenitive() + "</b>.");
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.ADDITIONAL_PAYMENT_TYPE_TEXT;
    }
}
