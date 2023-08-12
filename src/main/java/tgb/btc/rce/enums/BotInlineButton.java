package tgb.btc.rce.enums;

import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.vo.InlineButton;

public enum BotInlineButton {
    CANCEL(InlineButton.builder().text("Назад")
                   .inlineType(InlineType.CALLBACK_DATA)
                   .data(Command.BACK.getText())
                   .build()),
    USE_SAVED_WALLET(InlineButton.builder()
                             .text(BotProperties.BUTTONS_DESIGN.getString("USE_SAVED_WALLET"))
                             .data(BotStringConstants.USE_SAVED_WALLET)
                             .build());

    private final InlineButton button;

    BotInlineButton(InlineButton button) {
        this.button = button;
    }

    public InlineButton getButton() {
        return button;
    }
}
