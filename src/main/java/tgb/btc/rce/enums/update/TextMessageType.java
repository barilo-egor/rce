package tgb.btc.rce.enums.update;

import java.util.Objects;

public enum TextMessageType {
    SLASH;

    public static TextMessageType fromText(String text) {
        if (Objects.isNull(text) || text.isBlank()) {
            return null;
        }
        if (text.startsWith("/")) return SLASH;
        return null;
    }
}
