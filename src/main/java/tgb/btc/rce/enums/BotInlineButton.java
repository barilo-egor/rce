package tgb.btc.rce.enums;

import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.vo.InlineButton;

public enum BotInlineButton {
    CANCEL(InlineButton.builder().text("Назад")
            .inlineType(InlineType.CALLBACK_DATA)
            .data(Command.BACK.getText())
            .build()),
    USE_SAVED_WALLET(InlineButton.builder()
            .text(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("USE_SAVED_WALLET"))
            .data(BotStringConstants.USE_SAVED_WALLET)
            .build()),
    SHOW(InlineButton.builder()
            .text("Показать")
            .inlineType(InlineType.CALLBACK_DATA)
            .build());

    private final InlineButton button;

    BotInlineButton(InlineButton button) {
        this.button = button;
    }

    public InlineButton getButton() {
        return button;
    }
}
