package tgb.btc.rce.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BotMerchantService implements IBotMerchantService {

    private final IPaymentTypeService paymentTypeService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    public BotMerchantService(IPaymentTypeService paymentTypeService, IResponseSender responseSender,
                              ICallbackDataService callbackDataService, IKeyboardBuildService keyboardBuildService) {
        this.paymentTypeService = paymentTypeService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void sendRequestPaymentType(Merchant merchant, Long chatId) {
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndIsOnAndFiatCurrency(DealType.BUY, true, FiatCurrency.RUB);
        if (Objects.isNull(paymentTypes) || paymentTypes.isEmpty()) {
            responseSender.sendMessage(chatId, "Отсутствуют <b>включенные</b> типы оплаты на <b>покупку</b> для фиатной валюты <b>"
                    + FiatCurrency.RUB.getDisplayName() + "</b>.");
            return;
        }
        List<InlineButton> buttons = new ArrayList<>(paymentTypes
                .stream()
                .map(paymentType -> InlineButton.builder()
                        .text(paymentType.getName())
                        .data(callbackDataService.buildData(CallbackQueryData.PAYMENT_TYPE_BINDING, merchant.name(), paymentType.getPid()))
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build())
                .toList()
        );
        responseSender.sendMessage(chatId,
                "Выберите тип оплаты, к которому хотите выполнить привязку метода оплаты " + merchant.getDisplayName() + ". Типы оплаты отображены только для валюты \""
                        + FiatCurrency.RUB.getDisplayName() + "\".", keyboardBuildService.buildInline(buttons, 2));
    }

    @Override
    public void sendRequestMethod(Long chatId, String data, Integer messageId) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(data, 1));
        Long paymentTypePid = callbackDataService.getLongArgument(data, 2);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, messageId);

        List<InlineButton> buttons = new ArrayList<>(merchant.getMethodDescriptions().entrySet().stream()
                .map(entry -> InlineButton.builder()
                        .text(entry.getValue())
                        .data(callbackDataService.buildData(CallbackQueryData.PAYMENT_TYPE_METHOD, merchant.name(), paymentTypePid, entry.getKey()))
                        .build())
                .toList());
        if (!merchant.getHasBindPredicate().test(paymentType)) {
            responseSender.sendMessage(chatId, "Привязка типа оплаты <b>\"" + paymentType.getName()
                            + "\"</b> к методу оплаты " + merchant.getDisplayName() + " отсутствует. Выберите новый метод.",
                    keyboardBuildService.buildInline(buttons, 1));
        } else {
            buttons.add(InlineButton.builder()
                    .text("❌ Удалить привязку")
                    .data(callbackDataService.buildData(CallbackQueryData.PAYMENT_TYPE_METHOD, merchant.name(), paymentTypePid))
                    .build());
            String methodName = merchant.getGetMethodDescriptionFunction().apply(paymentType);
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                            + "\"</b> привязан к " + merchant.getDisplayName() + " методу оплаты <b>\""
                            + methodName + "\"</b>.",
                    keyboardBuildService.buildInline(buttons, 1));
        }
    }

    @Override
    public void saveBind(Long chatId, String data, Integer messageId) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(data, 1));
        Long paymentTypePid = callbackDataService.getLongArgument(data, 2);
        String paymentTypeName = callbackDataService.getArgument(data, 3);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, messageId);
        if (Objects.nonNull(paymentTypeName)) {
            merchant.getSetMethodFromNameConsumer().accept(paymentType, paymentTypeName);
            String methodName = merchant.getGetMethodDescriptionFunction().apply(paymentType);
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> связан с " + merchant.getDisplayName() + " методом оплаты <b>\"" + methodName
                    + "\"</b>.");
        } else {
            merchant.getSetEmptyMethodConsumer().accept(paymentType);
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> отвязан от " + merchant.getDisplayName() + " метода оплаты.");
        }
        paymentTypeService.save(paymentType);
        log.debug("Пользователь {} установил новое значение метода для мерчанта {}: {}", chatId, merchant.getDisplayName(), paymentTypeName);
    }
}
