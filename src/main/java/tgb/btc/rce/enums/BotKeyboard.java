package tgb.btc.rce.enums;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

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
    CRYPTO_CURRENCIES(KeyboardUtil.buildReply(List.of(KeyboardUtil.getCryptoCurrencyButtons())));

    final ReplyKeyboard keyboard;

    BotKeyboard(ReplyKeyboard keyboard) {
        this.keyboard = keyboard;
    }

    public ReplyKeyboard getKeyboard() {
        return keyboard;
    }
}
