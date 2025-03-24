package tgb.btc.rce.service.impl;

import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
public class AlfaTeamBindingService {

    private final IResponseSender responseSender;

    private final IPaymentTypeService paymentTypeService;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    public AlfaTeamBindingService(IResponseSender responseSender, IPaymentTypeService paymentTypeService,
                                  ICallbackDataService callbackDataService, IKeyboardBuildService keyboardBuildService) {
        this.responseSender = responseSender;
        this.paymentTypeService = paymentTypeService;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
    }

    public void sendPaymentTypes(Long chatId, Predicate<PaymentType> hasBindPredicate,
                                 Merchant merchant) {
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
                        .data(callbackDataService.buildData(CallbackQueryData.ALFA_TEAM_PAYMENT_TYPE_BINDING,paymentType.getPid(), false, merchant.name()))
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build())
                .toList()
        );
        Optional<PaymentType> optionalPaymentType = paymentTypes.stream()
                .filter(hasBindPredicate)
                .findFirst();
        String messageText;
        if (optionalPaymentType.isPresent()) {
            messageText = "Текущая привязка  " + merchant.getDisplayName() + " к типу оплаты <b>\"" + optionalPaymentType.get().getName() + "\"</b>.\n";
            buttons.add(InlineButton.builder()
                    .text("❌ Удалить привязку")
                    .data(callbackDataService.buildData(CallbackQueryData.ALFA_TEAM_PAYMENT_TYPE_BINDING, optionalPaymentType.get().getPid(), true, merchant.name()))
                    .build());
        } else {
            messageText = "";
        }
        messageText = messageText + "Выберите тип оплаты, к которому хотите выполнить привязку метода оплаты " + merchant.getDisplayName()
                + ". Типы оплаты отображены только для валюты \""
                + FiatCurrency.RUB.getDisplayName() + "\".";
        responseSender.sendMessage(chatId, messageText, keyboardBuildService.buildInline(buttons, 2));
    }
}
