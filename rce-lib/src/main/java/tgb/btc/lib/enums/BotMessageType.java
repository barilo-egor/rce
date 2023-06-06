package tgb.btc.lib.enums;

import tgb.btc.lib.exception.BaseException;

import java.util.Arrays;

public enum BotMessageType {
    START("Стартовое"),
    DRAWS("Кн.Розыгрыши"),
    CONTACTS("Кн.Контакты"),
    SELL_BITCOIN("Кн.Продать"),

    ROULETTE("Кн.рулетка"),

    WON_LOTTERY("Выигр.лотереи"),
    LOSE_LOTTERY("Проигр.лотереи"),
    ADDITIONAL_DEAL_TEXT("Доп.текст заявки"),
    BOT_OFF("Выкл.бот");

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

    public static BotMessageType getByName(String name) {
        return Arrays.stream(BotMessageType.values()).filter(t -> t.name().equals(name)).findFirst()
                .orElseThrow(() -> new BaseException("Не найдено сообщение бота: " + name));
    }
}
