package tgb.btc.lib.enums;

import tgb.btc.lib.vo.ReplyButton;

public enum BotReplyButton {
    CANCEL(ReplyButton.builder()
            .text("Отмена")
            .build());

    private final ReplyButton button;

    BotReplyButton(ReplyButton button) {
        this.button = button;
    }

    public ReplyButton getButton() {
        return button;
    }
}
