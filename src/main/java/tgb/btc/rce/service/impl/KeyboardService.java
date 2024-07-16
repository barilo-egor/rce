package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgb.btc.library.constants.enums.CabinetButton;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.constants.enums.RPSElement;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.DeliveryType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.library.util.FiatCurrencyUtil;
import tgb.btc.library.util.properties.VariablePropertiesUtil;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotInlineButton;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.processors.calculator.InlineCalculator;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.CryptoCurrenciesDesignUtil;
import tgb.btc.rce.util.FiatCurrenciesDesignUtil;
import tgb.btc.rce.util.TurningCurrenciesUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.InlineCalculatorVO;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static tgb.btc.library.constants.enums.properties.PropertiesPath.RPS_PROPERTIES;
import static tgb.btc.rce.enums.InlineCalculatorButton.*;

@Service
public class KeyboardService implements IKeyboardService {

    private IPaymentTypeService paymentTypeService;

    private IKeyboardBuildService keyboardBuildService;

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
        TurningCurrenciesUtil.getSwitchedOnByDealType(dealType)
                .forEach(currency -> currencies.add(InlineButton.buildData(CryptoCurrenciesDesignUtil.getDisplayName(currency), currency.name())));
        currencies.add(keyboardBuildService.getInlineBackButton());
        return keyboardBuildService.buildInline(currencies);
    }

    @Override
    public ReplyKeyboard getFiatCurrencies() {
        List<InlineButton> buttons = FiatCurrencyUtil.getFiatCurrencies().stream()
                .map(fiatCurrency -> InlineButton.builder()
                        .text(FiatCurrenciesDesignUtil.getDisplayData(fiatCurrency))
                        .data(CallbackQueryUtil.buildCallbackData(Command.CHOOSING_FIAT_CURRENCY, fiatCurrency.name()))
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
        Integer numberOfColumns = PropertiesPath.FUNCTIONS_PROPERTIES.getInteger("payment.types.columns", null);
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
                        .text(Command.SHOW_DEAL.getText())
                        .data(Command.SHOW_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                + dealPid)
                        .build()
        ));
    }

    @Override
    public ReplyKeyboard getShowApiDeal(Long pid) {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text("Показать")
                        .data(Command.SHOW_API_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                + pid)
                        .build()
        ));
    }

    @Override
    public ReplyKeyboard getUseReferralDiscount(BigDecimal sumWithDiscount, BigDecimal dealAmount) {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text("Со скидкой, " + BigDecimalUtil.roundToPlainString(sumWithDiscount))
                        .data(BotStringConstants.USE_REFERRAL_DISCOUNT)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Без скидки, " + BigDecimalUtil.roundToPlainString(dealAmount))
                        .data(BotStringConstants.DONT_USE_REFERRAL_DISCOUNT)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                keyboardBuildService.getInlineBackButton()
        ));
    }

    @Override
    public ReplyKeyboard getPromoCode(BigDecimal sumWithDiscount, BigDecimal dealAmount) {
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .text(String.format(Command.USE_PROMO.getText(), BigDecimalUtil.roundToPlainString(sumWithDiscount)))
                        .data(BotStringConstants.USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text(String.format(Command.DONT_USE_PROMO.getText(), BigDecimalUtil.roundToPlainString(dealAmount)))
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
            inlineButtons.add(keyboardBuildService.createCallBackDataButton(string, Command.INLINE_CALCULATOR, NUMBER.getData(), string));
        }
        inlineButtons.add(keyboardBuildService.createCallBackDataButton(COMMA.getData(), Command.INLINE_CALCULATOR, COMMA.getData()));
        inlineButtons.add(keyboardBuildService.createCallBackDataButton(DEL.getData(), Command.INLINE_CALCULATOR, DEL.getData()));
        InlineButton backButton = BotInlineButton.CANCEL.getButton();
        backButton.setText(CANCEL.getData());
        inlineButtons.add(backButton);
        inlineButtons.add(keyboardBuildService.createCallBackDataButton(SWITCH_CALCULATOR.getData(), Command.INLINE_CALCULATOR, SWITCH_CALCULATOR.getData()));
        inlineButtons.add(keyboardBuildService.createCallBackDataButton(READY.getData(), Command.INLINE_CALCULATOR, READY.getData()));
        InlineCalculatorVO calculator = InlineCalculator.cache.get(chaId);
        String text = !calculator.getSwitched()
                ? calculator.getFiatCurrency().getFlag() + "Ввести сумму в " + calculator.getFiatCurrency().getCode().toUpperCase()
                : "\uD83D\uDD38Ввести сумму в " + calculator.getCryptoCurrency().getShortName().toUpperCase();
        List<InlineButton> currencySwitcher = Collections.singletonList(keyboardBuildService.createCallBackDataButton(text,
                Command.INLINE_CALCULATOR, CURRENCY_SWITCHER.getData()));
        List<List<InlineKeyboardButton>> rows = keyboardBuildService.buildInlineRows(inlineButtons, 3);
        rows.add(4, keyboardBuildService.buildInlineRows(currencySwitcher, 1).get(0));
        return keyboardBuildService.buildInlineByRows(rows);
    }

    @Override
    public ReplyKeyboard getInlineCalculatorSwitcher() {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(keyboardBuildService.createCallBackDataButton(SWITCH_TO_MAIN_CALCULATOR.getData(), Command.INLINE_CALCULATOR, SWITCH_TO_MAIN_CALCULATOR.getData()));
        buttons.add(keyboardBuildService.getInlineBackButton());
        return keyboardBuildService.buildInline(buttons);
    }

    @Override
    public ReplyKeyboard getDeliveryTypes(FiatCurrency fiatCurrency, DealType dealType, CryptoCurrency cryptoCurrency) {
        List<InlineButton> buttons = new ArrayList<>();
        Arrays.stream(DeliveryType.values()).forEach(x -> {
            String text = PropertiesPath.DESIGN_PROPERTIES.getString(x.name());
            if (DeliveryType.VIP.equals(x) &&
                    PropertiesPath.FUNCTIONS_PROPERTIES.getBoolean("vip.button.add.sum", false)) {
                Integer fix;
                try {
                    fix = VariablePropertiesUtil.getInt(VariableType.FIX_COMMISSION_VIP,
                            fiatCurrency, dealType, cryptoCurrency);
                } catch (NumberFormatException e) {
                    throw new BaseException("Значение фиксированной комиссии для " + DeliveryType.VIP.getDisplayName() + " должно быть целочисленным.");
                }
                text = text +  "(+" + fix + fiatCurrency.getGenitive() + ")";
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
        if (DeliveryKind.NONE.isCurrent()) {
            text = "Включить";
            deliveryKind = DeliveryKind.STANDARD;
        } else {
            text = "Выключить";
            deliveryKind = DeliveryKind.NONE;
        }
        return InlineButton.builder()
                .text(text)
                .data(CallbackQueryUtil.buildCallbackData(Command.TURN_PROCESS_DELIVERY, deliveryKind.name()))
                .build();
    }

    @Override
    public ReplyKeyboard getRPSRates() {
        List<InlineButton> buttons = new ArrayList<>();
        String[] sums = RPS_PROPERTIES.getString("sums").split(",");
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
                .text(element.getSymbol())
                .data(element.name())
                .build()));
        return keyboardBuildService.buildInlineSingleLast(buttons, 1, keyboardBuildService.getInlineBackButton());
    }


    public ReplyKeyboard getCabinetButtons() {
        List<InlineButton> buttons = new ArrayList<>();
        Arrays.stream(CabinetButton.values()).forEach(button -> buttons.add(InlineButton.builder()
                .text(button.getText())
                .data(button.name())
                .build()));
        return keyboardBuildService.buildInline(buttons, 1);
    }

}
