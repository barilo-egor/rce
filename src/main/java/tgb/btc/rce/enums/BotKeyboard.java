package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BotKeyboard {
    CANCEL(KeyboardUtil.buildReply(List.of(BotReplyButton.CANCEL.getButton()))),
    BUY_OR_SELL(KeyboardUtil.buildReply(List.of(
            ReplyButton.builder()
                    .text(BotStringConstants.BUY)
                    .build(),
            ReplyButton.builder()
                    .text(BotStringConstants.SELL)
                    .build(),
            BotReplyButton.CANCEL.getButton()
    ))),
    OPERATOR(KeyboardUtil.buildInline(List.of(
            InlineButton.builder()
                    .text("Связь с оператором")
                    .data(BotProperties.BOT_VARIABLE_PROPERTIES.getString(BotVariableType.OPERATOR_LINK.getKey()))
                    .inlineType(InlineType.URL)
                    .build()
    ))),
    CRYPTO_CURRENCIES(getCryptoCurrencyKeyboard()),
    FIAT_CURRENCIES(getFiatCurrenciesKeyboard());

    final ReplyKeyboard keyboard;

    BotKeyboard(ReplyKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    public ReplyKeyboard getKeyboard() {
        return keyboard;
    }

    private static ReplyKeyboardMarkup getCryptoCurrencyKeyboard() {
        return KeyboardUtil.buildReply(List.of(KeyboardUtil.getCryptoCurrencyButtons()));
    }

    private static ReplyKeyboardMarkup getFiatCurrenciesKeyboard() {
        return KeyboardUtil.buildReply(Arrays.stream(FiatCurrency.values())
                .map(fiatCurrency -> ReplyButton.builder().text(fiatCurrency.getCode()).build())
                .collect(Collectors.toList()));
    }

}
