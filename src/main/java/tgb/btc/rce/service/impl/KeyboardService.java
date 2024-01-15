package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.DeliveryType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.repository.bot.PaymentTypeRepository;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.library.util.FiatCurrencyUtil;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotInlineButton;
import tgb.btc.rce.enums.CalculatorType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.processors.InlineCalculator;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.InlineCalculatorVO;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static tgb.btc.rce.enums.InlineCalculatorButton.*;

@Service
public class KeyboardService {

    private static final CalculatorType CALCULATOR_TYPE =
            CalculatorType.valueOf(PropertiesPath.MODULES_PROPERTIES.getString("calculator.type"));

    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public ReplyKeyboard getCurrencies(DealType dealType) {
        List<InlineButton> currencies = new ArrayList<>();
        TurningCurrenciesUtil.getSwitchedOnByDealType(dealType)
                .forEach(currency -> currencies.add(InlineButton.buildData(CryptoCurrenciesDesignUtil.getDisplayName(currency), currency.name())));
        currencies.add(KeyboardUtil.INLINE_BACK_BUTTON);
        return KeyboardUtil.buildInline(currencies);
    }

    public ReplyKeyboard getFiatCurrencies() {
        List<InlineButton> buttons = FiatCurrencyUtil.getFiatCurrencies().stream()
                .map(fiatCurrency -> InlineButton.builder()
                        .text(FiatCurrenciesDesignUtil.getDisplayData(fiatCurrency))
                        .data(CallbackQueryUtil.buildCallbackData(Command.CHOOSING_FIAT_CURRENCY.getText(), fiatCurrency.name()))
                        .build())
                .collect(Collectors.toList());
        buttons.add(KeyboardUtil.INLINE_BACK_BUTTON);
        return KeyboardUtil.buildInline(buttons);
    }

    public ReplyKeyboard getPaymentTypes(DealType dealType, FiatCurrency fiatCurrency) {
        List<InlineButton> buttons =
                paymentTypeRepository.getByDealTypeAndIsOnAndFiatCurrency(dealType, true, fiatCurrency).stream()
                        .map(paymentType -> InlineButton.builder()
                                .text(paymentType.getName())
                                .data(paymentType.getPid().toString())
                                .inlineType(InlineType.CALLBACK_DATA)
                                .build())
                        .collect(Collectors.toList());
        Integer numberOfColumns = PropertiesPath.FUNCTIONS_PROPERTIES.getInteger("payment.types.columns", null);
        if (Objects.nonNull(numberOfColumns)) {
            return KeyboardUtil.buildInlineSingleLast(buttons, numberOfColumns, KeyboardUtil.INLINE_BACK_BUTTON);
        }
        buttons.add(KeyboardUtil.INLINE_BACK_BUTTON);
        return KeyboardUtil.buildInline(buttons);
    }

    public ReplyKeyboard getShowDeal(Long dealPid) {
        return KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text(Command.SHOW_DEAL.getText())
                        .data(Command.SHOW_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                + dealPid)
                        .build()
        ));
    }

    public ReplyKeyboard getShowApiDeal(Long pid) {
        return KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("Показать")
                        .data(Command.SHOW_API_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                + pid)
                        .build()
        ));
    }

    public ReplyKeyboard getUseReferralDiscount(BigDecimal sumWithDiscount, BigDecimal dealAmount) {
        return KeyboardUtil.buildInline(List.of(
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
                KeyboardUtil.INLINE_BACK_BUTTON
        ));
    }

    public ReplyKeyboard getPromoCode(BigDecimal sumWithDiscount, BigDecimal dealAmount) {
        return KeyboardUtil.buildInline(List.of(
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
                KeyboardUtil.INLINE_BACK_BUTTON
        ));
    }

    public ReplyKeyboard getInlineCalculator(Long chaId) {
        List<InlineButton> inlineButtons = new ArrayList<>();
        String[] strings = new String[]{"7", "8", "9", "4", "5", "6", "1", "2", "3", "0"};
        for (String string : strings) {
            inlineButtons.add(KeyboardUtil.createCallBackDataButton(string, Command.INLINE_CALCULATOR, NUMBER.getData(), string));
        }
        inlineButtons.add(KeyboardUtil.createCallBackDataButton(COMMA));
        inlineButtons.add(KeyboardUtil.createCallBackDataButton(DEL));
        InlineButton backButton = BotInlineButton.CANCEL.getButton();
        backButton.setText(CANCEL.getData());
        inlineButtons.add(backButton);
        inlineButtons.add(KeyboardUtil.createCallBackDataButton(SWITCH_CALCULATOR));
        inlineButtons.add(KeyboardUtil.createCallBackDataButton(READY));
        InlineCalculatorVO calculator = InlineCalculator.cache.get(chaId);
        String text = !calculator.getSwitched()
                ? calculator.getFiatCurrency().getFlag() + "Ввод суммы в " + calculator.getFiatCurrency().getCode().toUpperCase()
                : "\uD83D\uDD38Ввод суммы в " + calculator.getCryptoCurrency().getShortName().toUpperCase();
        List<InlineButton> currencySwitcher = Collections.singletonList(KeyboardUtil.createCallBackDataButton(text,
                Command.INLINE_CALCULATOR, CURRENCY_SWITCHER.getData()));
        List<List<InlineKeyboardButton>> rows = KeyboardUtil.buildInlineRows(inlineButtons, 3);
        rows.add(4, KeyboardUtil.buildInlineRows(currencySwitcher, 1).get(0));
        return KeyboardUtil.buildInlineByRows(rows);
    }

    public ReplyKeyboard getInlineCalculatorSwitcher() {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(KeyboardUtil.createCallBackDataButton(SWITCH_TO_MAIN_CALCULATOR));
        buttons.add(KeyboardUtil.INLINE_BACK_BUTTON);
        return KeyboardUtil.buildInline(buttons);
    }

    public ReplyKeyboard getDeliveryTypes() {
        List<InlineButton> buttons = new ArrayList<>();
        Arrays.stream(DeliveryType.values()).forEach(x -> buttons.add(InlineButton.builder()
                .text(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString(x.name()))
                .data(x.name())
                .inlineType(InlineType.CALLBACK_DATA)
                .build()));
        return KeyboardUtil.buildInlineSingleLast(buttons, 1, KeyboardUtil.INLINE_BACK_BUTTON);
    }

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
                .data(CallbackQueryUtil.buildCallbackData(Command.TURN_PROCESS_DELIVERY.getText(), deliveryKind.name()))
                .build();
    }

}
