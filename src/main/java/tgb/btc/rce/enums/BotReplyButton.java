package tgb.btc.rce.enums;

import tgb.btc.rce.vo.ReplyButton;

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
