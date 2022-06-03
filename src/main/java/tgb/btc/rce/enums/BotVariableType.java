package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

public enum BotVariableType {
    USD_COURSE("Курс доллара", "course.usd"),
    FIX("Фикс рублей", "fix.rub"),
    FIX_COMMISSION("Фикс комиссия", "commission.fix"),
    COMMISSION("Комиссия", "commission.main"),
    OPERATOR_LINK("Ссылка на оператора", "operator.link"),
    PROBABILITY("Шанс лотереи", "lottery.chance"),
    MIN_SUM_BUY_BTC("Мин.сумма BTC", "deal.buy.sum.min.btc"),

    MIN_SUM_BUY_LTC("Мин.сумма", "deal.buy.sum.min.ltc"),

    MIN_SUM_BUY_USDT("Мин.сумма", "deal.buy.sum.min.usdt"),
    TRANSACTION_COMMISSION("Транз.комиссия", "transaction.commission"),
    PROMO_CODE_DISCOUNT("Скидка от промокода", "promo.code.discount"),
    PROMO_CODE_NAME("Название промокода", "promo.code.name");

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