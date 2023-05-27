package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.TurningCurrenciesUtil;
import tgb.btc.rce.vo.InlineButton;

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
}
