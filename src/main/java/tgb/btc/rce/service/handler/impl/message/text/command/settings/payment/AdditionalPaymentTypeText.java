package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class AdditionalPaymentTypeText implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IPaymentTypeService paymentTypeService;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    public AdditionalPaymentTypeText(IResponseSender responseSender, IPaymentTypeService paymentTypeService,
                                     ICallbackDataService callbackDataService, IKeyboardBuildService keyboardBuildService) {
        this.responseSender = responseSender;
        this.paymentTypeService = paymentTypeService;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void handle(Message message) {
        List<PaymentType> paymentTypeList = paymentTypeService.getByDealType(DealType.BUY);
        List<InlineButton> buttons = paymentTypeList.stream()
                .map(paymentType ->
                        InlineButton.builder()
                                .text(paymentType.getName())
                                .data(callbackDataService.buildData(CallbackQueryData.ADDITIONAL_PAYMENT_TYPE_TEXT, paymentType.getPid()))
                                .build())
                .toList();
        responseSender.sendMessage(message.getChatId(), "Выберите тип оплаты на <b>" + DealType.BUY.getGenitive()
                + "</b> в котором хотите изменить добавочный к реквизитам текст.", keyboardBuildService.buildInline(buttons, 2));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.ADDITIONAL_PAYMENT_TYPE_TEXT;
    }
}
