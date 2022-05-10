package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

public enum BotMessageType {
    START("Стартовое"),
    DRAWS("Розыгрыши");

    final String displayName;

    BotMessageType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BotMessageType getByDisplayName(String name) {
        return Arrays.stream(BotMessageType.values()).filter(t -> t.getDisplayName().equals(name)).findFirst()
                .orElseThrow(() -> new BaseException("Не найдено сообщение бота: " + name));
    }
}
