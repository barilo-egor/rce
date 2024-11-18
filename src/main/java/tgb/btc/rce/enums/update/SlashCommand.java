package tgb.btc.rce.enums.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SlashCommand {
    START("/start");

    private final String text;
}
