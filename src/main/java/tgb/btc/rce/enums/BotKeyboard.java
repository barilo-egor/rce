package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum BotKeyboard {
    REPLY_CANCEL(KeyboardUtil.buildReply(List.of(BotReplyButton.CANCEL.getButton()))),
    INLINE_CANCEL(KeyboardUtil.buildInline(List.of(BotInlineButton.CANCEL.getButton()))),
    BUY_OR_SELL(KeyboardUtil.buildReply(List.of(
            ReplyButton.builder()
                    .text(DealType.BUY.getNominativeFirstLetterToUpper())
                    .build(),
            ReplyButton.builder()
                    .text(DealType.SELL.getNominativeFirstLetterToUpper())
                    .build(),
            BotReplyButton.CANCEL.getButton()
    ))),
    OPERATOR(KeyboardUtil.buildInline(List.of(
            InlineButton.builder()
                    .text("Связь с оператором")
                    .data(BotProperties.VARIABLE.getString(VariableType.OPERATOR_LINK.getKey()))
                    .inlineType(InlineType.URL)
                    .build()
    ))),
    CRYPTO_CURRENCIES(getCryptoCurrencyKeyboard()),
    FIAT_CURRENCIES(getFiatCurrenciesKeyboard()),
    BUILD_DEAL(KeyboardUtil.buildInline(List.of(
            InlineButton.builder()
                    .text(Command.PAID.getText())
                    .data(Command.PAID.name())
                    .inlineType(InlineType.CALLBACK_DATA)
                    .build(),
            InlineButton.builder()
                    .text(Command.CANCEL.getText())
                    .data(Command.CANCEL_DEAL.name())
                    .inlineType(InlineType.CALLBACK_DATA)
                    .build()
    ))),
    CANCEL_DEAL(KeyboardUtil.buildReply(List.of(
            ReplyButton.builder()
                    .text(Command.RECEIPTS_CANCEL_DEAL.getText())
                    .build())));

    final ReplyKeyboard keyboard;

    BotKeyboard(ReplyKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    public ReplyKeyboard getKeyboard() {
        return keyboard;
    }

    private static ReplyKeyboardMarkup getCryptoCurrencyKeyboard() {
        List<ReplyButton> buttons = new ArrayList<>(List.of(KeyboardUtil.getCryptoCurrencyButtons()));
        buttons.add(BotReplyButton.CANCEL.getButton());
        return KeyboardUtil.buildReply(buttons);
    }

    private static ReplyKeyboardMarkup getFiatCurrenciesKeyboard() {
        List<ReplyButton> buttons = Arrays.stream(FiatCurrency.values())
                .map(fiatCurrency -> ReplyButton.builder().text(fiatCurrency.getCode()).build())
                .collect(Collectors.toList());
        buttons.add(BotReplyButton.CANCEL.getButton());
        return KeyboardUtil.buildReply(buttons);
    }

}
