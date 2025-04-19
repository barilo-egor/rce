package tgb.btc.rce.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgb.btc.library.bean.bot.AutoConfirmConfig;
import tgb.btc.library.bean.bot.MerchantConfig;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.Merchant;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.DeliveryType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.web.merchant.AutoConfirmType;
import tgb.btc.library.interfaces.service.bean.bot.IMerchantConfigService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.HTMLTag;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IBotMerchantService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.*;

@Service
@Slf4j
public class BotMerchantService implements IBotMerchantService {

    private final IPaymentTypeService paymentTypeService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    private final IMerchantConfigService merchantConfigService;

    public BotMerchantService(IPaymentTypeService paymentTypeService, IResponseSender responseSender,
                              ICallbackDataService callbackDataService, IKeyboardBuildService keyboardBuildService,
                              IMerchantConfigService merchantConfigService) {
        this.paymentTypeService = paymentTypeService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
        this.merchantConfigService = merchantConfigService;
    }

    @Override
    public void sendRequestPaymentType(CallbackQuery callbackQuery) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        StringBuilder message = new StringBuilder();
        message.append("Текущие связанные методы оплаты мерчанта ")
                .append(HTMLTag.BOLD.wrap(merchant.getDisplayName()))
                .append(":\n");
        List<PaymentType> paymentTypeList = paymentTypeService.findAll();
        for (PaymentType paymentType : paymentTypeList) {
            if (merchant.getHasBindPredicate().test(paymentType)) {
                message.append("Метод ")
                        .append(HTMLTag.BOLD.wrap(merchant.getGetMethodDescriptionFunction().apply(paymentType)))
                        .append(" связан с типом оплаты ")
                        .append(HTMLTag.BOLD.wrap(paymentType.getName()))
                        .append("\n");
            }
        }
        message.append("\n").append("Выберите тип оплаты, к которому хотите привязать/отвязать метод мерчанта.");
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndFiatCurrency(DealType.BUY, FiatCurrency.RUB);
        List<InlineButton> buttons = new ArrayList<>();
        if (paymentTypes.isEmpty()) {
            message.append("\n\n‼️ ").append(HTMLTag.BOLD.wrap("Отсутствуют типы оплаты."));
        } else {
            buttons.addAll(paymentTypes
                    .stream()
                    .map(paymentType -> InlineButton.builder()
                            .text(paymentType.getName())
                            .data(callbackDataService.buildData(CallbackQueryData.PAYMENT_TYPE_BINDING, merchant.name(), paymentType.getPid()))
                            .inlineType(InlineType.CALLBACK_DATA)
                            .build())
                    .toList());
        }
        buttons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS_BINDING.name()).build());
        responseSender.sendEditedMessageText(callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                message.toString(),
                keyboardBuildService.buildInline(buttons, 2)
        );
    }

    @Override
    public void sendRequestMethod(Long chatId, String data, Integer messageId) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(data, 1));
        Long paymentTypePid = callbackDataService.getLongArgument(data, 2);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);

        List<InlineButton> buttons = new ArrayList<>(merchant.getMethodDescriptions().entrySet().stream()
                .map(entry -> InlineButton.builder()
                        .text(entry.getValue())
                        .data(callbackDataService.buildData(CallbackQueryData.PAYMENT_TYPE_METHOD, merchant.name(), paymentTypePid, entry.getKey()))
                        .build())
                .toList());
        if (!merchant.getHasBindPredicate().test(paymentType)) {
            buttons.add(InlineButton.builder().text("Назад").data(callbackDataService.buildData(CallbackQueryData.MERCHANT_BINDING, merchant.name())).build());
            responseSender.sendEditedMessageText(chatId, messageId, "Привязка типа оплаты <b>\"" + paymentType.getName()
                            + "\"</b> к методу оплаты " + merchant.getDisplayName() + " отсутствует. Выберите новый метод.",
                    keyboardBuildService.buildInline(buttons, 2));
        } else {
            buttons.add(InlineButton.builder()
                    .text("❌ Удалить привязку")
                    .data(callbackDataService.buildData(CallbackQueryData.PAYMENT_TYPE_METHOD, merchant.name(), paymentTypePid))
                    .build());
            String methodName = merchant.getGetMethodDescriptionFunction().apply(paymentType);
            buttons.add(InlineButton.builder().text("Назад").data(callbackDataService.buildData(CallbackQueryData.MERCHANT_BINDING, merchant.name())).build());
            responseSender.sendEditedMessageText(chatId, messageId, "Тип оплаты <b>\"" + paymentType.getName()
                            + "\"</b> привязан к " + merchant.getDisplayName() + " методу оплаты <b>\""
                            + methodName + "\"</b>.",
                    keyboardBuildService.buildInline(buttons, 2));
        }
    }

    @Override
    public void saveBind(Long chatId, String data, Integer messageId) {
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(data, 1));
        Long paymentTypePid = callbackDataService.getLongArgument(data, 2);
        String paymentTypeName = callbackDataService.getArgument(data, 3);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        List<InlineButton> buttons = List.of(InlineButton.builder()
                .text("Вернуться")
                .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_BINDING, merchant.name()))
                .build());
        if (Objects.nonNull(paymentTypeName)) {
            merchant.getSetMethodFromNameConsumer().accept(paymentType, paymentTypeName);
            String methodName = merchant.getGetMethodDescriptionFunction().apply(paymentType);
            responseSender.sendEditedMessageText(chatId, messageId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> связан с " + merchant.getDisplayName() + " методом оплаты <b>\"" + methodName
                    + "\"</b>.", buttons);
        } else {
            merchant.getSetEmptyMethodConsumer().accept(paymentType);
            responseSender.sendEditedMessageText(chatId, messageId,"Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> отвязан от " + merchant.getDisplayName() + " метода оплаты.", buttons);
        }
        paymentTypeService.save(paymentType);
        log.debug("Пользователь {} установил новое значение метода для мерчанта {}: {}", chatId, merchant.getDisplayName(), paymentTypeName);
    }

    @Override
    public void sendMerchantsMenu(Long chatId, Integer messageId) {
        List<InlineButton> buttonList = new ArrayList<>();
        buttonList.add(InlineButton.builder().text("Включение").data(CallbackQueryData.MERCHANTS_TURNING.name()).build());
        buttonList.add(InlineButton.builder().text("Очередь").data(CallbackQueryData.MERCHANTS_ORDER.name()).build());
        buttonList.add(InlineButton.builder().text("Максимальные суммы").data(CallbackQueryData.MERCHANTS_MAX_AMOUNTS.name()).build());
        buttonList.add(InlineButton.builder().text("Количество попыток").data(CallbackQueryData.MERCHANTS_ATTEMPTS_COUNT.name()).build());
        buttonList.add(InlineButton.builder().text("Задержка").data(CallbackQueryData.MERCHANTS_DELAY.name()).build());
        buttonList.add(InlineButton.builder().text("Привязка").data(CallbackQueryData.MERCHANTS_BINDING.name()).build());
        buttonList.add(InlineButton.builder().text("Автоподтверждение").data(CallbackQueryData.MERCHANTS_AUTO_CONFIRM.name()).build());
        String text = "Меню управления мерчантами.";
        ReplyKeyboard replyKeyboard = keyboardBuildService.buildInline(buttonList, 2);
        if (Objects.isNull(messageId)) {
            responseSender.sendMessage(chatId, text, replyKeyboard);
        } else {
            responseSender.sendEditedMessageText(chatId, messageId, text, replyKeyboard);
        }
    }

    @Override
    public void sendIsOn(Long chatId, Integer messageId) {
        List<MerchantConfig> merchantConfigs = merchantConfigService.findAll();
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (MerchantConfig merchantConfig : merchantConfigs) {
            boolean isOn = Objects.nonNull(merchantConfig.getIsOn()) && merchantConfig.getIsOn();
            String merchantDisplayName = merchantConfig.getMerchant().getDisplayName();
            inlineButtons.add(InlineButton.builder()
                    .text(isOn ? "\uD83D\uDFE2 " + merchantDisplayName : "\uD83D\uDD34 " + merchantDisplayName)
                    .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_TURNING, merchantConfig.getMerchant().name()))
                    .build()
            );
        }
        inlineButtons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS.name()).build());
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "\uD83D\uDFE2 - мерчант включен на текущий момент, \uD83D\uDD34 - выключен. Для включения/выключения нажмите по кнопке с названием мерчанта.",
                keyboardBuildService.buildInline(inlineButtons, 2)
        );
    }

    @Override
    public void sendOrder(Long chatId, Integer messageId) {
        List<MerchantConfig> merchantConfigs = merchantConfigService.findAllSortedByMerchantOrder();
        List<InlineButton> inlineButtons = new ArrayList<>();
        int i = 1;
        for (MerchantConfig merchantConfig : merchantConfigs) {
            if (i == 1) {
                inlineButtons.add(InlineButton.builder().text("-").data(CallbackQueryData.NONE.name()).build());
            } else {
                inlineButtons.add(InlineButton.builder()
                        .text("⬆️")
                        .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_ORDER, merchantConfig.getMerchant(), true))
                        .build());
            }
            boolean isOn = Objects.nonNull(merchantConfig.getIsOn()) && merchantConfig.getIsOn();
            String merchantDisplayName = merchantConfig.getMerchant().getDisplayName();
            inlineButtons.add(InlineButton.builder()
                    .text(isOn ? "\uD83D\uDFE2 " + merchantDisplayName : "\uD83D\uDD34 " + merchantDisplayName)
                    .data(CallbackQueryData.NONE.name())
                    .build()
            );
            if (i < merchantConfigs.size()) {
                inlineButtons.add(InlineButton.builder()
                        .text("⬇️")
                        .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_ORDER, merchantConfig.getMerchant(), false))
                        .build());
            } else {
                inlineButtons.add(InlineButton.builder().text("-").data(CallbackQueryData.NONE.name()).build());
            }
            i++;
        }
        inlineButtons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS.name()).build());
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "Выполните требуемые перемещения нажимая на кнопки ⬇️ и ⬆️.",
                keyboardBuildService.buildInline(inlineButtons, 3)
        );
    }

    @Override
    public void sendMaxAmounts(Long chatId, Integer messageId) {
        List<MerchantConfig> merchantConfigs = merchantConfigService.findAll();
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (MerchantConfig merchantConfig : merchantConfigs) {
            inlineButtons.add(InlineButton.builder()
                            .text(merchantConfig.getMerchant().getDisplayName() + " - " + merchantConfig.getMaxAmount())
                            .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_MAX_AMOUNT, merchantConfig.getMerchant().name()))
                    .build());
        }
        inlineButtons.add(InlineButton.builder().text("Для всех").data(callbackDataService.buildData(CallbackQueryData.MERCHANT_MAX_AMOUNT, "ALL")).build());
        inlineButtons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS.name()).build());
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "Текущие максимальные суммы мерчантов. Для изменения нажмите на нужного мерчанта.",
                keyboardBuildService.buildInline(inlineButtons, 2)
        );
    }

    @Override
    public void sendBindingMerchants(Long chatId, Integer messageId) {
        List<InlineButton> buttons = new ArrayList<>();
        for (Merchant merchant : Merchant.values()) {
            buttons.add(InlineButton.builder()
                    .text(merchant.getDisplayName())
                    .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_BINDING, merchant.name()))
                    .build()
            );
        }
        buttons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS.name()).build());
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "Выберите мерчанта, к которому хотите привязать или отвязать тип оплаты.",
                keyboardBuildService.buildInline(buttons, 2)
        );
    }

    @Override
    public void sendAutoConfirmMenu(Long chatId, Integer messageId) {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(InlineButton.builder().text("Статусы").data(CallbackQueryData.MERCHANTS_STATUSES.name()).build());
        buttons.add(InlineButton.builder().text("Включение").data(CallbackQueryData.MERCHANTS_AUTO_CONFIRM_TURNING.name()).build());
        buttons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS.name()).build());
        responseSender.sendEditedMessageText(chatId, messageId, "Меню управления автоподтверждением сделок.",
                keyboardBuildService.buildInline(buttons, 2));
    }

    @Override
    public void sendStatusesMerchants(Long chatId, Integer messageId) {
        List<InlineButton> buttons = new ArrayList<>();
        for (Merchant merchant : Merchant.values()) {
            buttons.add(InlineButton.builder()
                    .text(merchant.getDisplayName())
                    .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_STATUSES, merchant.name()))
                    .build()
            );
        }
        buttons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS_AUTO_CONFIRM.name()).build());
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "Выберите мерчанта, у которого хотите отредактировать статусы для автоподтверждения.",
                keyboardBuildService.buildInline(buttons, 2)
        );
    }

    @Override
    public void sendMerchantStatuses(Long chatId, Integer messageId, Merchant merchant) {
        MerchantConfig merchantConfig = merchantConfigService.getMerchantConfig(merchant);
        List<InlineButton> buttons = new ArrayList<>();
        for (Map.Entry<String, String> entry: merchant.getStatusesSupplier().get().entrySet()) {
            String emoji = merchantConfig.getSuccessStatuses().stream()
                    .anyMatch(status -> status.getStatus().equals(entry.getKey()))
                    ? "✅"
                    : "";
            buttons.add(InlineButton.builder()
                    .text(emoji + " " + entry.getValue())
                    .data(callbackDataService.buildData(CallbackQueryData.CHANGE_MERCHANT_STATUS, merchant.name(), entry.getKey()))
                    .build());
        }
        buttons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS_STATUSES.name()).build());
        responseSender.sendEditedMessageText(chatId, messageId, "Статусы с галочкой (✅) являются одобренными для автоподтверждения. " +
                "Для того, чтобы сделки в выбранном статусе автоматически подтвеждались и выводились нажмите на статус.", buttons);
    }

    @Override
    public void sendAutoConfirmsMerchants(Long chatId, Integer messageId) {
        List<InlineButton> buttons = new ArrayList<>();
        for (Merchant merchant : Merchant.values()) {
            buttons.add(InlineButton.builder()
                    .text(merchant.getDisplayName())
                    .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_AUTO_CONFIRM, merchant.name()))
                    .build()
            );
        }
        buttons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS_AUTO_CONFIRM.name()).build());
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "Выберите мерчанта, у которого хотите включить/выключить автоподтверждение.",
                keyboardBuildService.buildInline(buttons, 2)
        );
    }

    @Override
    public void sendMerchantAutoConfirms(Merchant merchant, Long chatId, Integer messageId) {
        MerchantConfig merchantConfig = merchantConfigService.getMerchantConfig(merchant);
        List<AutoConfirmConfig> autoConfirmConfigs = merchantConfig.getConfirmConfigs();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (CryptoCurrency cryptoCurrency : CryptoCurrency.CURRENCIES_WITH_AUTO_WITHDRAWAL) {
            List<InlineKeyboardButton> cryptoCurrencyRow = new ArrayList<>();
            cryptoCurrencyRow.add(InlineKeyboardButton.builder()
                    .text("\uD83D\uDD3D " + cryptoCurrency.name() + " \uD83D\uDD3D")
                    .callbackData(CallbackQueryData.NONE.name())
                    .build()
            );
            rows.add(cryptoCurrencyRow);

            List<InlineKeyboardButton> deliveryTypesRow = new ArrayList<>();
            for (DeliveryType deliveryType : DeliveryType.values()) {
                deliveryTypesRow.add(InlineKeyboardButton.builder()
                        .text("\uD83D\uDD3D " + deliveryType.name() + " \uD83D\uDD3D")
                        .callbackData(CallbackQueryData.NONE.name())
                        .build()
                );
            }
            rows.add(deliveryTypesRow);
            List<AutoConfirmType> autoConfirmTypes = CryptoCurrency.BITCOIN.equals(cryptoCurrency)
                    ? Arrays.asList(AutoConfirmType.values())
                    : List.of(AutoConfirmType.AUTO_WITHDRAWAL);
            List<InlineKeyboardButton> autoConfirmTypesRow = new ArrayList<>();
            for (DeliveryType deliveryType : DeliveryType.values()) {
                for (AutoConfirmType autoConfirmType : autoConfirmTypes) {
                    Optional<AutoConfirmConfig> autoConfirmConfigOptional =
                            merchantConfig.getAutoConfirmConfig(cryptoCurrency, deliveryType, autoConfirmType);
                    String emoji = autoConfirmConfigOptional.isPresent() ? "✅" : "";
                    autoConfirmTypesRow.add(InlineKeyboardButton.builder()
                            .text(emoji + " " + autoConfirmType.getDescription())
                            .callbackData(callbackDataService.buildData(CallbackQueryData.AUTO_CONFIRM,
                                    merchant.name(), cryptoCurrency, deliveryType.name(), autoConfirmType.name()))
                            .build());
                }
            }
            rows.add(autoConfirmTypesRow);
        }
        rows.add(List.of(InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData(CallbackQueryData.MERCHANTS_AUTO_CONFIRM_TURNING.name())
                .build()));
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "Сконфигурируйте автоподтверждение для мерчанта " + HTMLTag.BOLD.wrap(merchant.getDisplayName()) + ". " +
                        "Для этого поставьте галочку на нужной опции (автовывод либо пул), либо уберите галочку, " +
                        "чтобы отключить автоподтверждение для определенной криптовалюты и типа доставки.\n\n" +
                        HTMLTag.BOLD.wrap("Автовывод") + " - сделки, у которых статус мерчанта изменится на одобренный, будут автоматически выводится отдельной транзакцией.\n" +
                        HTMLTag.BOLD.wrap("Пул") + " - сделки, у которых статус мерчанта изменится на одобренный, будут автоматически добавляться в пул.",
                keyboardBuildService.buildInlineByRows(rows)
        );
    }

    @Override
    public void sendAttemptsCount(Long chatId, Integer messageId) {
        List<MerchantConfig> merchantConfigs = merchantConfigService.findAll();
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (MerchantConfig merchantConfig : merchantConfigs) {
            inlineButtons.add(InlineButton.builder()
                    .text(merchantConfig.getMerchant().getDisplayName() + " - " + merchantConfig.getAttemptsCount())
                    .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_ATTEMPTS_COUNT, merchantConfig.getMerchant().name()))
                    .build());
        }
        inlineButtons.add(InlineButton.builder().text("Для всех").data(callbackDataService.buildData(CallbackQueryData.MERCHANT_ATTEMPTS_COUNT, "ALL")).build());
        inlineButtons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS.name()).build());
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "Текущие количества попыток мерчантов. Для изменения нажмите на нужного мерчанта.",
                keyboardBuildService.buildInline(inlineButtons, 2)
        );
    }

    @Override
    public void sendDelay(Long chatId, Integer messageId) {
        List<MerchantConfig> merchantConfigs = merchantConfigService.findAll();
        List<InlineButton> inlineButtons = new ArrayList<>();
        for (MerchantConfig merchantConfig : merchantConfigs) {
            inlineButtons.add(InlineButton.builder()
                    .text(merchantConfig.getMerchant().getDisplayName() + " - " + merchantConfig.getDelay())
                    .data(callbackDataService.buildData(CallbackQueryData.MERCHANT_DELAY, merchantConfig.getMerchant().name()))
                    .build());
        }
        inlineButtons.add(InlineButton.builder().text("Для всех").data(callbackDataService.buildData(CallbackQueryData.MERCHANT_DELAY, "ALL")).build());
        inlineButtons.add(InlineButton.builder().text("Назад").data(CallbackQueryData.MERCHANTS.name()).build());
        responseSender.sendEditedMessageText(
                chatId,
                messageId,
                "Текущие задержки между попытками мерчантов. Для изменения нажмите на нужного мерчанта.",
                keyboardBuildService.buildInline(inlineButtons, 2)
        );
    }

}
