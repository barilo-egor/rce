package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.rce.vo.ReplyButton;

@AllArgsConstructor
@Getter
public enum BotReplyButton {
    CANCEL(ReplyButton.builder()
            .text("Отмена")
            .build());

    private final ReplyButton button;

    public String getText() {
        return button.getText();
    }
}
