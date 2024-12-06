package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.vo.InlineButton;

@Getter
@AllArgsConstructor
public enum BotInlineButton {
    CANCEL(InlineButton.builder().text("Назад")
            .inlineType(InlineType.CALLBACK_DATA)
            .data(CallbackQueryData.BACK.name())
            .build());

    private final InlineButton button;
}
