package tgb.btc.lib.enums;


import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.vo.InlineButton;

public enum BotInlineButton {
    CANCEL(InlineButton.builder().text("Назад")
                   .inlineType(InlineType.CALLBACK_DATA)
                   .data(Command.BACK.getText())
                   .build()),
    USE_SAVED_WALLET(InlineButton.builder()
                             .text("Использовать сохраненный адрес")
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
