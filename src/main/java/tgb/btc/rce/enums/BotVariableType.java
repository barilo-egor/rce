package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

public enum BotVariableType {
    USD_COURSE("Курс доллара"),
    FIX_RU("Фикс рублей"),
    FIX_COMMISSION("Фикс комиссия РФ"),
    COMMISSION("Комиссия"),
    OPERATOR_LINK("Ссылка на оператора"),
    PROBABILITY("Шанс лотереи"),
    MIN_SUM("Мин.сумма"),
    TRANSACTION_COMMISSION("Транз.комиссия");

    final String displayName;

    BotVariableType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static BotVariableType getByDisplayName(String name) {
        return Arrays.stream(BotVariableType.values()).filter(t -> t.getDisplayName().equals(name)).findFirst()
                .orElseThrow(() -> new BaseException("Не найдена переменна бота: " + name));
    }
}