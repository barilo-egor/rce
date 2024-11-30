package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgb.btc.library.constants.enums.CabinetButton;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.constants.enums.RPSElement;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.DeliveryType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.enums.ICabinetButtonService;
import tgb.btc.library.interfaces.enums.IDeliveryTypeService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.library.service.process.RPSService;
import tgb.btc.library.service.properties.*;
import tgb.btc.rce.constants.BotCacheNames;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotInlineButton;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.impl.callback.settings.DealReportsCallbackHandler;
import tgb.btc.rce.service.handler.impl.state.deal.InlineCalculatorHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.processors.support.ExchangeService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.ICryptoCurrenciesDesignService;
import tgb.btc.rce.service.util.ITurningCurrenciesService;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.InlineCalculatorVO;
import tgb.btc.rce.vo.ReplyButton;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static tgb.btc.rce.enums.InlineCalculatorButton.*;

@Service
public class KeyboardService implements IKeyboardService {

    private IPaymentTypeService paymentTypeService;

    private IKeyboardBuildService keyboardBuildService;

    private ICryptoCurrenciesDesignService cryptoCurrenciesDesignService;

    private ButtonsDesignPropertiesReader buttonsDesignPropertiesReader;

    private ITurningCurrenciesService turningCurrenciesService;

    private IDeliveryTypeService deliveryTypeService;

    private ICabinetButtonService cabinetButtonService;

    private RPSService rpsService;

    private VariablePropertiesReader variablePropertiesReader;

    private IFiatCurrencyService fiatCurrencyService;

    private IBigDecimalService bigDecimalService;

    private IModule<DeliveryKind> deliveryKindModule;

    private FunctionsPropertiesReader functionsPropertiesReader;

    private DesignPropertiesReader designPropertiesReader;

    private RPSPropertiesReader rpsPropertiesReader;

    private ICallbackDataService callbackDataService;

    @Autowired
    public void setCallbackDataService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Autowired
    public void setRpsPropertiesReader(RPSPropertiesReader rpsPropertiesReader) {
        this.rpsPropertiesReader = rpsPropertiesReader;
    }

    @Autowired
    public void setDesignPropertiesReader(DesignPropertiesReader designPropertiesReader) {
        this.designPropertiesReader = designPropertiesReader;
    }

    @Autowired
    public void setFunctionsPropertiesReader(FunctionsPropertiesReader functionsPropertiesReader) {
        this.functionsPropertiesReader = functionsPropertiesReader;
    }

    @Autowired
    public void setDeliveryKindModule(IModule<DeliveryKind> deliveryKindModule) {
        this.deliveryKindModule = deliveryKindModule;
    }

    @Autowired
    public void setBigDecimalService(IBigDecimalService bigDecimalService) {
        this.bigDecimalService = bigDecimalService;
    }

    @Autowired
    public void setFiatCurrencyService(IFiatCurrencyService fiatCurrencyService) {
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setRpsService(RPSService rpsService) {
        this.rpsService = rpsService;
    }

    @Autowired
    public void setCabinetButtonService(ICabinetButtonService cabinetButtonService) {
        this.cabinetButtonService = cabinetButtonService;
    }

    @Autowired
    public void setDeliveryTypeService(IDeliveryTypeService deliveryTypeService) {
        this.deliveryTypeService = deliveryTypeService;
    }

    @Autowired
    public void setTurningCurrenciesService(ITurningCurrenciesService turningCurrenciesService) {
        this.turningCurrenciesService = turningCurrenciesService;
    }

    @Autowired
    public void setButtonsDesignPropertiesReader(ButtonsDesignPropertiesReader buttonsDesignPropertiesReader) {
        this.buttonsDesignPropertiesReader = buttonsDesignPropertiesReader;
    }

    @Autowired
    public void setCryptoCurrenciesDesignService(ICryptoCurrenciesDesignService cryptoCurrenciesDesignService) {
        this.cryptoCurrenciesDesignService = cryptoCurrenciesDesignService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Override
    public ReplyKeyboard getCurrencies(DealType dealType) {
        List<InlineButton> currencies = new ArrayList<>();
        turningCurrenciesService.getSwitchedOnByDealType(dealType)
                .forEach(currency -> currencies.add(InlineButton.buildData(cryptoCurrenciesDesignService.getDisplayName(currency), currency.name())));
        currencies.add(keyboardBuildService.getInlineBackButton());
        return keyboardBuildService.buildInline(currencies);
    }

    @Override
    public ReplyKeyboard getFiatCurrencies() {
        List<InlineButton> buttons = fiatCurrencyService.getFiatCurrencies().stream()
                .map(fiatCurrency -> InlineButton.builder()
                        .text(buttonsDesignPropertiesReader.getString(fiatCurrency.name()))
                        .data(callbackDataService.buildData(CallbackQueryData.CHOOSING_FIAT_CURRENCY, fiatCurrency.name()))
                        .build())
                .collect(Collectors.toList());
        buttons.add(keyboardBuildService.getInlineBackButton());
        return keyboardBuildService.buildInline(buttons);
    }

    @Override
    public ReplyKeyboard getPaymentTypes(DealType dealType, FiatCurrency fiatCurrency) {
        List<InlineButton> buttons =
                paymentTypeService.getByDealTypeAndIsOnAndFiatCurrency(dealType, true, fiatCurrency).stream()
                        .map(paymentType -> InlineButton.builder()
                                .text(paymentType.getName())
                                .data(paymentType.getPid().toString())
                                .inlineType(InlineType.CALLBACK_DATA)
                                .build())
                        .collect(Collectors.toList());
        Integer numberOfColumns = functionsPropertiesReader.getInteger("payment.types.columns", null);
        if (Objects.nonNull(numberOfColumns)) {
            return keyboardBuildService.buildInlineSingleLast(buttons, numberOfColumns, keyboardBuildService.getInlineBackButton());
        }
        buttons.add(keyboardBuildService.getInlineBackButton());
        return keyboardBuildService.buildInline(buttons);
    }

    @Override
    public ReplyKeyboard getShowDeal(Long dealPid) {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text("Показать")
                        .data(callbackDataService.buildData(CallbackQueryData.SHOW_DEAL, dealPid))
                        .build()
        ));
    }

    @Override
    public ReplyKeyboard getShowApiDeal(Long pid) {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text("Показать")
                        .data(callbackDataService.buildData(CallbackQueryData.SHOW_API_DEAL, pid))
                        .build()
        ));
    }

    @Override
    public ReplyKeyboard getUseReferralDiscount(BigDecimal sumWithDiscount, BigDecimal dealAmount) {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text("Со скидкой, " + bigDecimalService.roundToPlainString(sumWithDiscount))
                        .data(BotStringConstants.USE_REFERRAL_DISCOUNT)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Без скидки, " + bigDecimalService.roundToPlainString(dealAmount))
                        .data(BotStringConstants.DONT_USE_REFERRAL_DISCOUNT)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                keyboardBuildService.getInlineBackButton()
        ));
    }

    @Override
    @Cacheable(BotCacheNames.PROMO_CODE_KEYBOARD)
    public ReplyKeyboard getPromoCode(BigDecimal sumWithDiscount, BigDecimal dealAmount) {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text("Использовать промокод")
                        .data(BotStringConstants.USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Не использовать промокод")
                        .data(BotStringConstants.DONT_USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                keyboardBuildService.getInlineBackButton()
        ));
    }

    @Override
    public ReplyKeyboard getInlineCalculator(Long chaId) {
        List<InlineButton> inlineButtons = new ArrayList<>();
        String[] strings = new String[]{"7", "8", "9", "4", "5", "6", "1", "2", "3", "0"};
        for (String string : strings) {
            inlineButtons.add(keyboardBuildService.createCallBackDataButton(string, CallbackQueryData.INLINE_CALCULATOR, NUMBER.getData(), string));
        }
        inlineButtons.add(keyboardBuildService.createCallBackDataButton(COMMA.getData(), CallbackQueryData.INLINE_CALCULATOR, COMMA.getData()));
        inlineButtons.add(keyboardBuildService.createCallBackDataButton(DEL.getData(), CallbackQueryData.INLINE_CALCULATOR, DEL.getData()));
        InlineButton backButton = BotInlineButton.CANCEL.getButton();
        backButton.setText(CANCEL.getData());
        inlineButtons.add(backButton);
        inlineButtons.add(keyboardBuildService.createCallBackDataButton(SWITCH_CALCULATOR.getData(), CallbackQueryData.INLINE_CALCULATOR, SWITCH_CALCULATOR.getData()));
        inlineButtons.add(keyboardBuildService.createCallBackDataButton(READY.getData(), CallbackQueryData.INLINE_CALCULATOR, READY.getData()));
        InlineCalculatorVO calculator = InlineCalculatorHandler.cache.get(chaId);
        String text = !calculator.getSwitched()
                ? calculator.getFiatCurrency().getFlag() + "Ввести сумму в " + calculator.getFiatCurrency().getCode().toUpperCase()
                : "\uD83D\uDD38Ввести сумму в " + calculator.getCryptoCurrency().getShortName().toUpperCase();
        List<InlineButton> currencySwitcher = Collections.singletonList(keyboardBuildService.createCallBackDataButton(text,
                CallbackQueryData.INLINE_CALCULATOR, CURRENCY_SWITCHER.getData()));
        List<List<InlineKeyboardButton>> rows = keyboardBuildService.buildInlineRows(inlineButtons, 3);
        rows.add(4, keyboardBuildService.buildInlineRows(currencySwitcher, 1).get(0));
        return keyboardBuildService.buildInlineByRows(rows);
    }

    @Override
    public ReplyKeyboard getInlineCalculatorSwitcher() {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(keyboardBuildService.createCallBackDataButton(SWITCH_TO_MAIN_CALCULATOR.getData(), CallbackQueryData.INLINE_CALCULATOR, SWITCH_TO_MAIN_CALCULATOR.getData()));
        buttons.add(keyboardBuildService.getInlineBackButton());
        return keyboardBuildService.buildInline(buttons);
    }

    @Override
    public ReplyKeyboard getDeliveryTypes(FiatCurrency fiatCurrency, DealType dealType, CryptoCurrency cryptoCurrency) {
        List<InlineButton> buttons = new ArrayList<>();
        Arrays.stream(DeliveryType.values()).forEach(x -> {
            String text = designPropertiesReader.getString(x.name());
            if (DeliveryType.VIP.equals(x) &&
                    functionsPropertiesReader.getBoolean("vip.button.add.sum", false)) {
                Integer fix;
                try {
                    fix = variablePropertiesReader.getInt(VariableType.FIX_COMMISSION_VIP,
                            fiatCurrency, dealType, cryptoCurrency);
                } catch (NumberFormatException e) {
                    throw new BaseException("Значение фиксированной комиссии для " + deliveryTypeService.getDisplayName(DeliveryType.VIP) + " должно быть целочисленным.");
                }
                text = text + "(+" + fix + fiatCurrency.getGenitive() + ")";
            }
            buttons.add(InlineButton.builder()
                    .text(text)
                    .data(x.name())
                    .inlineType(InlineType.CALLBACK_DATA)
                    .build());
        });
        return keyboardBuildService.buildInlineSingleLast(buttons, 1, keyboardBuildService.getInlineBackButton());
    }

    @Override
    public InlineButton getDeliveryTypeButton() {
        String text;
        DeliveryKind deliveryKind;
        if (deliveryKindModule.isCurrent(DeliveryKind.NONE)) {
            text = "Включить";
            deliveryKind = DeliveryKind.STANDARD;
        } else {
            text = "Выключить";
            deliveryKind = DeliveryKind.NONE;
        }
        return InlineButton.builder()
                .text(text)
                .data(callbackDataService.buildData(CallbackQueryData.TURN_PROCESS_DELIVERY, deliveryKind.name()))
                .build();
    }

    @Override
    public ReplyKeyboard getRPSRates() {
        List<InlineButton> buttons = new ArrayList<>();
        String[] sums = rpsPropertiesReader.getString("sums").split(",");
        Arrays.asList(sums).forEach(sum -> buttons.add(InlineButton.builder()
                .text(sum)
                .data(sum)
                .inlineType(InlineType.CALLBACK_DATA)
                .build()));
        return keyboardBuildService.buildInlineSingleLast(buttons, 1, keyboardBuildService.getInlineBackButton());
    }

    @Override
    public ReplyKeyboard getRPSElements() {
        List<InlineButton> buttons = new ArrayList<>();
        Arrays.stream(RPSElement.values()).forEach(element -> buttons.add(InlineButton.builder()
                .text(rpsService.getSymbol(element))
                .data(element.name())
                .build()));
        return keyboardBuildService.buildInlineSingleLast(buttons, 1, keyboardBuildService.getInlineBackButton());
    }


    public ReplyKeyboard getCabinetButtons() {
        List<InlineButton> buttons = new ArrayList<>();
        Arrays.stream(CabinetButton.values()).forEach(cabinetButton -> buttons.add(InlineButton.builder()
                .text(cabinetButtonService.getText(cabinetButton))
                .data(cabinetButton.name())
                .build()));
        return keyboardBuildService.buildInline(buttons, 1);
    }

    @Override
    public ReplyKeyboard getReplyCancel() {
        return keyboardBuildService.buildReply(List.of(BotReplyButton.CANCEL.getButton()));
    }

    @Override
    public ReplyKeyboard getInlineCancel() {
        return keyboardBuildService.buildInline(List.of(BotInlineButton.CANCEL.getButton()));
    }

    @Override
    public ReplyKeyboard getBuyOrSell() {
        return keyboardBuildService.buildReply(List.of(
                ReplyButton.builder()
                        .text(DealType.BUY.getNominativeFirstLetterToUpper())
                        .build(),
                ReplyButton.builder()
                        .text(DealType.SELL.getNominativeFirstLetterToUpper())
                        .build(),
                BotReplyButton.CANCEL.getButton()
        ));
    }

    @Override
    @Cacheable(BotCacheNames.INLINE_DEAL_TYPES_BUTTONS)
    public ReplyKeyboard getInlineDealTypes(CallbackQueryData callbackQueryData, FiatCurrency fiatCurrency) {
        List<InlineButton> buttons = new ArrayList<>();
        for (DealType dealType: DealType.values()) {
            String data = Objects.nonNull(fiatCurrency)
                    ? callbackDataService.buildData(callbackQueryData, dealType.name(), fiatCurrency.name())
                    : callbackDataService.buildData(callbackQueryData, dealType.name());
            buttons.add(InlineButton.builder()
                            .text(dealType.getNominativeFirstLetterToUpper())
                            .data(data)
                    .build());
        }
        return keyboardBuildService.buildInline(buttons);
    }

    @Override
    public ReplyKeyboard getOperator() {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text("Связь с оператором")
                        .data(variablePropertiesReader.getString(VariableType.OPERATOR_LINK.getKey()))
                        .inlineType(InlineType.URL)
                        .build()
        ));
    }

    @Override
    public ReplyKeyboardMarkup getFiatCurrenciesKeyboard() {
        List<ReplyButton> buttons = Arrays.stream(FiatCurrency.values())
                .map(fiatCurrency -> ReplyButton.builder().text(fiatCurrency.getCode()).build())
                .collect(Collectors.toList());
        buttons.add(BotReplyButton.CANCEL.getButton());
        return keyboardBuildService.buildReply(buttons);
    }

    @Override
    @Cacheable(BotCacheNames.FIAT_INLINE_BUTTONS)
    public ReplyKeyboard getInlineFiats(CallbackQueryData callbackQueryData) {
        List<InlineButton> buttons = new ArrayList<>();
        for (FiatCurrency fiatCurrency: fiatCurrencyService.getFiatCurrencies()) {
            buttons.add(
                    InlineButton.builder()
                            .text(fiatCurrency.getDisplayName())
                            .data(callbackDataService.buildData(callbackQueryData, fiatCurrency.name()))
                            .build()
            );
        }
        buttons.add(InlineButton.builder().text("Отмена").data(CallbackQueryData.INLINE_DELETE.name()).build());
        return keyboardBuildService.buildInline(buttons, 1);
    }

    @Override
    public ReplyKeyboard getBuildDeal() {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text(ExchangeService.PAID_TEXT)
                        .data(ExchangeService.PAID_DATA)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Отменить заявку")
                        .data("cancel")
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build()
        ));
    }

    @Override
    public ReplyKeyboard getCancelDeal() {
        return keyboardBuildService.buildReply(List.of(
                ReplyButton.builder()
                        .text(TextCommand.RECEIPTS_CANCEL_DEAL.getText())
                        .build()));
    }

    @Override
    @Cacheable(BotCacheNames.DEAL_REPORTS)
    public ReplyKeyboard getDealReports() {
        return keyboardBuildService.buildInline(List.of(
            InlineButton.builder().text("За сегодня").data(callbackDataService.buildData(CallbackQueryData.DEAL_REPORTS, DealReportsCallbackHandler.TODAY)).build(),
            InlineButton.builder().text("За десять дней").data(callbackDataService.buildData(CallbackQueryData.DEAL_REPORTS, DealReportsCallbackHandler.TEN_DAYS)).build(),
            InlineButton.builder().text("За месяц").data(callbackDataService.buildData(CallbackQueryData.DEAL_REPORTS, DealReportsCallbackHandler.MONTH)).build(),
            InlineButton.builder().text("За дату").data(callbackDataService.buildData(CallbackQueryData.DEAL_REPORTS, DealReportsCallbackHandler.DATE)).build()
        ));
    }
}
