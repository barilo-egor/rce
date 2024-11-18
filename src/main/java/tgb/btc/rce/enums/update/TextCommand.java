package tgb.btc.rce.enums.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TextCommand {
    BUY_BITCOIN("Купить"),
    SELL_BITCOIN("Продать");

    private final String text;
}
