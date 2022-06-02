package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

public enum BotVariableType {
    USD_COURSE("Курс доллара", "course.usd"),
    FIX_RU("Фикс рублей", "fix.rub"),
    FIX_COMMISSION("Фикс комиссия", "commission.fix"),
    COMMISSION("Комиссия", "commission.main"),
    OPERATOR_LINK("Ссылка на оператора", "operator.link"),
    PROBABILITY("Шанс лотереи", "lottery.chance"),
    MIN_SUM("Мин.сумма", "deal.sum.min"),
    TRANSACTION_COMMISSION("Транз.комиссия", "transaction.commission"),
    MIN_WITHDRAWAL_OF_FUNDS_SUM("Мин.сумма вывода средств", "withdrawal.sum.min"),
    BTC_BORDER("Граница bitcoin", "border.btc"),
    LITECOIN_BORDER("Граница bitcoin", "litecoin.btc"),
    TETHER_BORDER("Граница bitcoin", "tether.btc");

    final String displayName;
    final String key;

    BotVariableType(String displayName, String key) {
        this.displayName = displayName;
        this.key = key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getKey() {
        return key;
    }

    public static BotVariableType getByDisplayName(String name) {
        return Arrays.stream(BotVariableType.values()).filter(t -> t.getDisplayName().equals(name)).findFirst()
                .orElseThrow(() -> new BaseException("Не найдена переменна бота: " + name));
    }
}