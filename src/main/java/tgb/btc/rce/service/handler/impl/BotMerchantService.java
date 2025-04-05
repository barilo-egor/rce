package tgb.btc.rce.service.handler.impl;

import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.web.merchant.evopay.EvoPayPaymentMethod;
import tgb.btc.library.constants.enums.web.merchant.onlypays.OnlyPaysPaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.*;

@Service
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
        CallbackQueryData callbackQueryData = switch (merchant) {
            case NONE -> null;
            case PAYSCROW -> null;
            case DASH_PAY -> null;
            case ALFA_TEAM -> null;
            case ALFA_TEAM_TJS -> null;
            case ALFA_TEAM_VTB -> null;
            case ALFA_TEAM_ALFA -> null;
            case ALFA_TEAM_SBER -> null;
            case PAY_POINTS -> null;
            case ONLY_PAYS -> CallbackQueryData.ONLY_PAYS_BINDING;
            case EVO_PAY -> CallbackQueryData.EVO_PAY_BINDING;
        };
        List<InlineButton> buttons = new ArrayList<>(paymentTypes
                .stream()
                .map(paymentType -> InlineButton.builder()
                        .text(paymentType.getName())
                        .data(callbackDataService.buildData(callbackQueryData, paymentType.getPid()))
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build())
                .toList()
        );
        responseSender.sendMessage(chatId,
                "Выберите тип оплаты, к которому хотите выполнить привязку метода оплаты " + merchant.getDisplayName() + ". Типы оплаты отображены только для валюты \""
                        + FiatCurrency.RUB.getDisplayName() + "\".", keyboardBuildService.buildInline(buttons, 2));
    }

    @Override
    public void sendRequestMethod(Merchant merchant, Long chatId, String data, Integer messageId) {
        Long paymentTypePid = callbackDataService.getLongArgument(data, 1);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, messageId);
        CallbackQueryData callbackQueryData = switch (merchant) {
            case NONE -> null;
            case PAYSCROW -> null;
            case DASH_PAY -> null;
            case ALFA_TEAM -> null;
            case ALFA_TEAM_TJS -> null;
            case ALFA_TEAM_VTB -> null;
            case ALFA_TEAM_ALFA -> null;
            case ALFA_TEAM_SBER -> null;
            case PAY_POINTS -> null;
            case ONLY_PAYS -> CallbackQueryData.ONLY_PAYS_METHOD;
            case EVO_PAY -> CallbackQueryData.EVO_PAY_METHOD;
        };
        Map<String, String> buttonsTextAndData = new LinkedHashMap<>();
        switch (merchant) {
            case ONLY_PAYS:
                for (OnlyPaysPaymentType pt: OnlyPaysPaymentType.values()) {
                    buttonsTextAndData.put(pt.name(), pt.getDescription());
                }
                break;
            case EVO_PAY:
                for (EvoPayPaymentMethod pm: EvoPayPaymentMethod.values()) {
                    buttonsTextAndData.put(pm.name(), pm.getDescription());
                }
                break;
        }
        List<InlineButton> buttons = new ArrayList<>(buttonsTextAndData.entrySet().stream()
                .map(entry -> InlineButton.builder().text(entry.getValue()).data(entry.getKey()).build())
                .toList());
        boolean hasBind = switch (merchant) {
            case NONE -> false;
            case PAYSCROW -> false;
            case DASH_PAY -> false;
            case ALFA_TEAM -> false;
            case ALFA_TEAM_TJS -> false;
            case ALFA_TEAM_VTB -> false;
            case ALFA_TEAM_ALFA -> false;
            case ALFA_TEAM_SBER -> false;
            case PAY_POINTS -> false;
            case ONLY_PAYS -> Objects.nonNull(paymentType.getOnlyPaysPaymentType());
            case EVO_PAY -> Objects.nonNull(paymentType.getEvoPayPaymentMethod());
        };
        if (hasBind) {
            responseSender.sendMessage(chatId, "Привязка типа оплаты <b>\"" + paymentType.getName()
                            + "\"</b> к методу оплаты " + merchant.getDisplayName() + " отсутствует. Выберите новый метод.",
                    keyboardBuildService.buildInline(buttons, 1));
        } else {
            buttons.add(InlineButton.builder()
                    .text("❌ Удалить привязку")
                    .data(callbackDataService.buildData(callbackQueryData, paymentTypePid))
                    .build());
            String methodName = switch (merchant) {
                case NONE -> null;
                case PAYSCROW -> null;
                case DASH_PAY -> null;
                case ALFA_TEAM -> null;
                case ALFA_TEAM_TJS -> null;
                case ALFA_TEAM_VTB -> null;
                case ALFA_TEAM_ALFA -> null;
                case ALFA_TEAM_SBER -> null;
                case PAY_POINTS -> null;
                case ONLY_PAYS -> paymentType.getOnlyPaysPaymentType().getDescription();
                case EVO_PAY -> paymentType.getEvoPayPaymentMethod().getDescription();
            };
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                            + "\"</b> привязан к " + merchant.getDisplayName() + " методу оплаты <b>\""
                            + methodName + "\"</b>.",
                    keyboardBuildService.buildInline(buttons, 1));
        }
    }

    @Override
    public void saveBind(Merchant merchant, Long chatId, String data, Integer messageId) {
        Long paymentTypePid = callbackDataService.getLongArgument(data, 1);
        String paymentTypeName = callbackDataService.getArgument(data, 2);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, messageId);
        if (Objects.nonNull(paymentTypeName)) {
            String methodName = null;
            switch (merchant) {
                case ONLY_PAYS -> {
                    OnlyPaysPaymentType onlyPaysPaymentType = OnlyPaysPaymentType.valueOf(paymentTypeName);
                    methodName = onlyPaysPaymentType.getDescription();
                    paymentType.setOnlyPaysPaymentType(onlyPaysPaymentType);
                }
                case EVO_PAY -> {
                    EvoPayPaymentMethod evoPayPaymentMethod = EvoPayPaymentMethod.valueOf(paymentTypeName);
                    methodName = evoPayPaymentMethod.getDescription();
                    paymentType.setEvoPayPaymentMethod(evoPayPaymentMethod);
                }
            }
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> связан с " + merchant.getDisplayName() + " методом оплаты <b>\"" + methodName
                    + "\"</b>.");
        } else {
            paymentType.setOnlyPaysPaymentType(null);
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> отвязан от " + merchant.getDisplayName() + " метода оплаты.");
        }
        paymentTypeService.save(paymentType);
    }
}
