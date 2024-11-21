package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.vo.ReplyButton;

@AllArgsConstructor
@Getter
public enum BotReplyButton {
    CANCEL(ReplyButton.builder()
            .text(TextCommand.CANCEL.getText())
            .build());

    private final ReplyButton button;

    public String getText() {
        return button.getText();
    }
}
