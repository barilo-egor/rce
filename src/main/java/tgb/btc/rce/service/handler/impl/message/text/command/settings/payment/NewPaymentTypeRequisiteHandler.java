package tgb.btc.rce.service.handler.impl.message.text.command.settings.payment;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NewPaymentTypeRequisiteHandler implements ITextCommandHandler {

    private final IFiatCurrencyService fiatCurrencyService;

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    private final IPaymentTypeService paymentTypeService;

    private final ICallbackDataService callbackDataService;

    public NewPaymentTypeRequisiteHandler(IFiatCurrencyService fiatCurrencyService, IResponseSender responseSender,
                                          IKeyboardService keyboardService, IPaymentTypeService paymentTypeService,
                                          ICallbackDataService callbackDataService) {
        this.fiatCurrencyService = fiatCurrencyService;
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
        this.paymentTypeService = paymentTypeService;
        this.callbackDataService = callbackDataService;
    }


    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        if (fiatCurrencyService.isFew()) {
            responseSender.sendMessage(chatId, "Выберите фиатную валюту для создания реквизита покупки.",
                    keyboardService.getInlineFiats(CallbackQueryData.FIAT_CREATE_REQUISITE));
            return;
        }
        sendPaymentTypes(chatId, fiatCurrencyService.getFirst(), null);
    }

    public void sendPaymentTypes(Long chatId, FiatCurrency fiatCurrency, Integer messageId) {
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndFiatCurrency(DealType.BUY, fiatCurrency);
        if (CollectionUtils.isEmpty(paymentTypes)) {
            responseSender.sendMessage(chatId, "Список тип оплат на " + DealType.BUY.getAccusative() + "-"
                    + fiatCurrency.getDisplayName() + " пуст.");
            return;
        }
        List<InlineButton> buttons = paymentTypes.stream()
                .map(paymentType -> InlineButton.builder()
                        .text(paymentType.getName())
                        .data(callbackDataService.buildData(
                                CallbackQueryData.PAYMENT_TYPE_CREATE_REQUISITE,
                                fiatCurrency.name(),
                                paymentType.getPid())
                        )
                        .build())
                .collect(Collectors.toList());
        buttons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());
        if (Objects.isNull(messageId)) {
            responseSender.sendMessage(chatId, "Выберите тип оплаты для создания реквизита покупки.",
                    buttons);
        } else {
            responseSender.sendEditedMessageText(chatId, messageId, "Выберите тип оплаты для создания реквизита покупки.",
                    buttons);
        }
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.NEW_PAYMENT_TYPE_REQUISITE;
    }
}
