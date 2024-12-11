package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HTMLTag {
    CODE("<code>", "</code>"),
    BOLD("<b>", "</b>"),;

    final String openTag;
    final String closeTag;
}
