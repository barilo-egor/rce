package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

public enum BotVariableType {
    USD_COURSE("Курс доллара", "course.usd"),
    FIX_BUY("Фикс рублей покупка", "fix.rub.buy"),
    FIX_SELL("Фикс рублей продажа", "fix.rub.sell"),
    FIX_COMMISSION_BUY("Фикс комиссия покупка", "commission.fix.buy"),
    FIX_COMMISSION_SELL("Фикс комиссия продажа", "commission.fix.sell"),
    COMMISSION_BUY("Комиссия покупка", "commission.main.buy"),
    COMMISSION_SELL("Комиссия продажа", "commission.main.sell"),
    OPERATOR_LINK("Ссылка на оператора", "operator.link"),
    PROBABILITY("Шанс лотереи", "lottery.chance"),

    MIN_SUM_BUY("Мин.сумма пок.", "deal.buy.sum.min"),
    MIN_SUM_BUY_BTC("Мин.сумма пок.BTC", "deal.buy.sum.min.btc"),
    MIN_SUM_BUY_LTC("Мин.сумма пок.LTC", "deal.buy.sum.min.ltc"),
    MIN_SUM_BUY_USDT("Мин.сумма пок.USDT", "deal.buy.sum.min.usdt"),
    MIN_SUM_BUY_XMR("Мин.сумма пок.USDT", "deal.buy.sum.min.xmr"),

    MIN_SUM_SELL("Мин.сумма прод.", "deal.sell.sum.min"),
    MIN_SUM_SELL_BTC("Мин.сумма прод.BTC", "deal.sell.sum.min.btc"),
    MIN_SUM_SELL_LTC("Мин.сумма прод.LTC", "deal.sell.sum.min.ltc"),
    MIN_SUM_SELL_USDT("Мин.сумма прод.USDT", "deal.sell.sum.min.usdt"),
    MIN_SUM_SELL_XMR("Мин.сумма прод.USDT", "deal.sell.sum.min.xmr"),

    TRANSACTION_COMMISSION("Транз.комиссия", "transaction.commission"),
    TRANSACTION_COMMISSION_BTC("Транз.комиссия", "transaction.commission.btc"),
    TRANSACTION_COMMISSION_LTC("Транз.комиссия", "transaction.commission.ltc"),
    TRANSACTION_COMMISSION_USDT("Транз.комиссия", "transaction.commission.usdt"),
    TRANSACTION_COMMISSION_XMR("Транз.комиссия", "transaction.commission.xmr"),

    PROMO_CODE_DISCOUNT("Скидка от промокода", "promo.code.discount"),
    PROMO_CODE_NAME("Название промокода", "promo.code.name"),
    DEAL_ACTIVE_TIME("Время активности заявки", "deal.active.time"),
    WALLET("Кошелек BTC", "wallet"),
    WALLET_BTC("Кошелек BTC", "wallet.btc"),
    WALLET_LTC("Кошелек LTC", "wallet.ltc"),
    WALLET_USDT("Кошелек USDT", "wallet.usdt"),
    WALLET_MONERO("Кошелек XMR", "wallet.xmr"),
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