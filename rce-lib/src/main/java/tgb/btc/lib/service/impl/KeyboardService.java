package tgb.btc.lib.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.*;
import tgb.btc.lib.repository.PaymentTypeRepository;
import tgb.btc.lib.service.processors.InlineCalculator;
import tgb.btc.lib.util.*;
import tgb.btc.lib.vo.InlineButton;
import tgb.btc.lib.vo.InlineCalculatorVO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static tgb.btc.lib.enums.InlineCalculatorButton.*;

@Service
public class KeyboardService {

    private static final CalculatorType CALCULATOR_TYPE =
            CalculatorType.valueOf(BotProperties.MODULES_PROPERTIES.getString("calculator.type"));

    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public ReplyKeyboard getCurrencies(DealType dealType) {
        List<InlineButton> currencies = new ArrayList<>();
        TurningCurrenciesUtil.getSwitchedOnByDealType(dealType)
                .forEach(currency -> currencies.add(InlineButton.buildData(currency.getDisplayName(), currency.name())));
        currencies.add(KeyboardUtil.INLINE_BACK_BUTTON);
        return KeyboardUtil.buildInline(currencies);
    }

    public ReplyKeyboard getCalculator(FiatCurrency fiatCurrency, CryptoCurrency currency, DealType dealType) {
        switch (CALCULATOR_TYPE) {
            case INLINE_QUERY:
                return KeyboardUtil.buildInline(List.of(
                        InlineButton.builder()
                                .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                                .text("Калькулятор")
                                .data(fiatCurrency.getCode() + "-" + dealType.getKey() + "-" + currency.getShortName())
                                .build(),
                        KeyboardUtil.INLINE_BACK_BUTTON), 1);
            default:
                return KeyboardUtil.buildInline(List.of(KeyboardUtil.INLINE_BACK_BUTTON), 1);
        }
    }

    public ReplyKeyboard getFiatCurrencies() {
        List<InlineButton> buttons = FiatCurrencyUtil.getFiatCurrencies().stream()
                .map(fiatCurrency -> InlineButton.builder()
                        .text(fiatCurrency.getCode().toUpperCase())
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

    public ReplyKeyboard getUseReferralDiscount(BigDecimal sumWithDiscount, BigDecimal dealAmount) {
        return KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("Со скидкой, " + BigDecimalUtil.toPlainString(sumWithDiscount))
                        .data(BotStringConstants.USE_REFERRAL_DISCOUNT)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Без скидки, " + BigDecimalUtil.toPlainString(dealAmount))
                        .data(BotStringConstants.DONT_USE_REFERRAL_DISCOUNT)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON
        ));
    }

    public ReplyKeyboard getPromoCode(BigDecimal sumWithDiscount, BigDecimal dealAmount) {
        return KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("Использовать, " + BigDecimalUtil.roundToPlainString(sumWithDiscount))
                        .data(BotStringConstants.USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Без промокода, " + BigDecimalUtil.roundToPlainString(dealAmount))
                        .data(BotStringConstants.DONT_USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON
        ));
    }

    public ReplyKeyboard getCalculator(Long chaId) {
        List<InlineButton> inlineButtons = new ArrayList<>();
        List<InlineButton> currencySwitcher = null;
        for (InlineCalculatorButton button : InlineCalculatorButton.values()) {
            switch (button) {
                case NUMBER:
                    String[] strings = new String[]{"7", "8", "9", "4", "5", "6", "1", "2", "3", "0"};
                    for (String string : strings) {
                        inlineButtons.add(KeyboardUtil.createCallBackDataButton(string, Command.INLINE_CALCULATOR, NUMBER.getData(), string));
                    }
                    break;
                case CURRENCY_SWITCHER:
                    InlineCalculatorVO calculator = InlineCalculator.cache.get(chaId);
                    String text;
                    if (!calculator.getSwitched()) {
                        String flag = FiatCurrency.RUB.equals(calculator.getFiatCurrency())
                                ? "\uD83C\uDDF7\uD83C\uDDFA"
                                : "\uD83C\uDDE7\uD83C\uDDFE";
                        text = flag + "Ввод суммы в " + calculator.getFiatCurrency().getCode().toUpperCase();
                    } else {
                        text = "\uD83D\uDD38Ввод суммы в " + calculator.getCryptoCurrency().getShortName().toUpperCase();
                    }
                    currencySwitcher = Collections.singletonList(KeyboardUtil.createCallBackDataButton(text,
                            Command.INLINE_CALCULATOR, CURRENCY_SWITCHER.getData()));
                    break;
                case CANCEL:
                    InlineButton backButton = BotInlineButton.CANCEL.getButton();
                    backButton.setText(CANCEL.getData());
                    inlineButtons.add(backButton);
                    break;
                case SWITCH_TO_MAIN_CALCULATOR:
                    break;
                default:
                    inlineButtons.add(KeyboardUtil.createCallBackDataButton(button));
            }
        }
        List<List<InlineKeyboardButton>> rows = KeyboardUtil.buildInlineRows(inlineButtons, 3);
        rows.add(4, KeyboardUtil.buildInlineRows(currencySwitcher,1).get(0));
        return KeyboardUtil.buildInlineByRows(rows);
    }

    public ReplyKeyboard getInlineCalculatorSwitcher() {
        List<InlineButton> buttons = new ArrayList<>();
        buttons.add(KeyboardUtil.createCallBackDataButton(SWITCH_TO_MAIN_CALCULATOR));
        buttons.add(KeyboardUtil.INLINE_BACK_BUTTON);
        return KeyboardUtil.buildInline(buttons);
    }
}
