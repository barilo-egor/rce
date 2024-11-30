package tgb.btc.rce.enums;

import lombok.Getter;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.vo.InlineButton;

@Getter
public enum BotInlineButton {
    CANCEL(InlineButton.builder().text("Назад")
            .inlineType(InlineType.CALLBACK_DATA)
            .data(CallbackQueryData.BACK.name())
            .build());

    private final InlineButton button;

    BotInlineButton(InlineButton button) {
        this.button = button;
    }

}
