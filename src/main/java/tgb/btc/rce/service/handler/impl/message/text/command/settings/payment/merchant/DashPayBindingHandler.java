package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment.merchant;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;

@Service
public class DashPayBindingHandler implements ITextCommandHandler {

    private final IPaymentTypeService paymentTypeService;

    private final ICallbackDataService callbackDataService;

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    public DashPayBindingHandler(IPaymentTypeService paymentTypeService, ICallbackDataService callbackDataService,
                                 IResponseSender responseSender, IKeyboardBuildService keyboardBuildService) {
        this.paymentTypeService = paymentTypeService;
        this.callbackDataService = callbackDataService;
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndIsOnAndFiatCurrency(DealType.BUY, true, FiatCurrency.RUB);
        if (Objects.isNull(paymentTypes) || paymentTypes.isEmpty()) {
            responseSender.sendMessage(chatId, "Отсутствуют <b>включенные</b> типы оплаты на <b>покупку</b> для фиатной валюты <b>"
                    + FiatCurrency.RUB.getDisplayName() + "</b>.");
            return;
        }
        List<InlineButton> buttons = new java.util.ArrayList<>(paymentTypes
                .stream()
                        .map(paymentType -> InlineButton.builder()
                                .text(paymentType.getName())
                                .data(callbackDataService.buildData(CallbackQueryData.DASH_PAY_PAYMENT_TYPE, paymentType.getPid()))
                                .inlineType(InlineType.CALLBACK_DATA)
                                .build())
                        .toList()
        );
        responseSender.sendMessage(chatId,
                "Выберите тип оплаты, к которому хотите выполнить привязку метода оплаты DashPay. Типы оплаты отображены только для валюты \""
                        + FiatCurrency.RUB.getDisplayName() + "\".", keyboardBuildService.buildInline(buttons, 2));
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.DASH_PAY_BINDING;
    }
}
