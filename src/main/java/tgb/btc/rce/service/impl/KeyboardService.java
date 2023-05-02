package tgb.btc.rce.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.TurningCurrenciesUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyboardService {

    public ReplyKeyboard getCurrencies(DealType dealType) {
        List<InlineButton> currencies = new ArrayList<>();
        TurningCurrenciesUtil.getSwitchedOnByDealType(dealType)
                .forEach(currency -> currencies.add(InlineButton.buildData(currency.getDisplayName(), currency.name())));
        currencies.add(KeyboardUtil.INLINE_BACK_BUTTON);
        return KeyboardUtil.buildInline(currencies);
    }

    public ReplyKeyboard getCalculator(CryptoCurrency currency, DealType dealType) {
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
    }
}
