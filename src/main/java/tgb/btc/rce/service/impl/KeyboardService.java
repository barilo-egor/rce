package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
}
