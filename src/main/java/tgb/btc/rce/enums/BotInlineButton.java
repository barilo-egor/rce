package tgb.btc.rce.enums;

import tgb.btc.rce.vo.InlineButton;

public enum BotInlineButton {
    CANCEL(InlineButton.builder().text("Назад")
            .inlineType(InlineType.CALLBACK_DATA)
            .data(Command.BACK.name())
            .build());

    private final InlineButton button;

    BotInlineButton(InlineButton button) {
        this.button = button;
    }

    public InlineButton getButton() {
        return button;
    }
}
