package tgb.btc.rce.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.util.FiatCurrenciesUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.TurningCurrenciesUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeyboardService {

    private static final CalculatorType CALCULATOR_TYPE =
            CalculatorType.valueOf(BotProperties.MODULES_PROPERTIES.getString("calculator.type"));
    ;

    public ReplyKeyboard getCurrencies(DealType dealType) {
        List<InlineButton> currencies = new ArrayList<>();
        TurningCurrenciesUtil.getSwitchedOnByDealType(dealType)
                .forEach(currency -> currencies.add(InlineButton.buildData(currency.getDisplayName(), currency.name())));
        currencies.add(KeyboardUtil.INLINE_BACK_BUTTON);
        return KeyboardUtil.buildInline(currencies);
    }

    public ReplyKeyboard getCalculator(CryptoCurrency currency, DealType dealType) {
        switch (CALCULATOR_TYPE) {
            case INLINE_QUERY:
                String operation = DealType.BUY.equals(dealType)
                        ? "-buy"
                        : "-sell";
                return KeyboardUtil.buildInline(List.of(
                        InlineButton.builder()
                                .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                                .text("Калькулятор")
                                .data(currency.getShortName() + operation + " ")
                                .build(),
                        KeyboardUtil.INLINE_BACK_BUTTON), 1);
            default:
                return null;
        }
    }

    public ReplyKeyboard getFiatCurrencies() {
        List<ReplyButton> buttons = FiatCurrenciesUtil.getFiatCurrencies().stream()
                .map(fiatCurrency -> ReplyButton.builder()
                        .text(fiatCurrency.getCode().toUpperCase())
                        .build())
                .collect(Collectors.toList());
        buttons.add(BotReplyButton.CANCEL.getButton());
        return KeyboardUtil.buildReply(buttons, false);
    }
}
