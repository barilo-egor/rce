package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

public enum BotVariableType {
    USD_COURSE("Курс доллара", "course.usd"),
    FIX("Фикс рублей покупка", "fix"),
    FIX_COMMISSION("Фикс комиссия", "commission.fix"),
    COMMISSION("Комиссия", "commission"),
    OPERATOR_LINK("Ссылка на оператора", "operator.link"),
    PROBABILITY("Шанс лотереи", "lottery.chance"),
    MIN_SUM("Мин.сумма пок.", "deal.min.sum"),
    TRANSACTION_COMMISSION("Транз.комиссия", "transaction.commission"),
    PROMO_CODE_DISCOUNT("Скидка от промокода", "promo.code.discount"),
    PROMO_CODE_NAME("Название промокода", "promo.code.name"),
    DEAL_ACTIVE_TIME("Время активности заявки", "deal.active.time"),
    WALLET("Кошелек BTC", "wallet"),
    REFERRAL_PERCENT("Процент рефералов", "referral.percent"),
    REFERRAL_MIN_SUM("Мин.сумма вывода", "referral.min.sum"),
    CHANNEL_CHAT_ID("Айди канала", "channel.chat.id"),
    REVIEW_PRISE("Вознаграждение", "review.prise"),
    USDT_COURSE("Курс USDT", "usdt.course"),
    DEAL_RANK_DISCOUNT_ENABLE("Ранговая скидка для всех", "deal.rank.discount.enable");

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

    public String getKey(CryptoCurrency cryptoCurrency) {
        return key.concat("." + cryptoCurrency.getShortName());
    }

    public String getKey(DealType dealType, CryptoCurrency cryptoCurrency) {
        return key.concat("." + dealType.getKey()).concat("." + cryptoCurrency.getShortName());
    }

    public static BotVariableType getByDisplayName(String name) {
        return Arrays.stream(BotVariableType.values()).filter(t -> t.getDisplayName().equals(name)).findFirst()
                .orElseThrow(() -> new BaseException("Не найдена переменна бота: " + name));
    }
}