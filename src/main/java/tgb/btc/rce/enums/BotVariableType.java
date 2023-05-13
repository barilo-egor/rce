package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

public enum BotVariableType {
    USD_COURSE("Курс доллара", "course.usd"),

    FIX_BUY("Фикс рублей покупка", "fix.rub.buy"),
    FIX_BUY_BTC("Фикс рублей покупка", "fix.rub.buy.btc"),
    FIX_BUY_LTC("Фикс рублей покупка", "fix.rub.buy.ltc"),
    FIX_BUY_USDT("Фикс рублей покупка", "fix.rub.buy.usdt"),
    FIX_BUY_XMR("Фикс рублей покупка", "fix.rub.buy.xmr"),

    FIX_SELL("Фикс рублей продажа", "fix.rub.sell"),
    FIX_SELL_BTC("Фикс рублей продажа", "fix.rub.sell.btc"),
    FIX_SELL_LTC("Фикс рублей продажа", "fix.rub.sell.ltc"),
    FIX_SELL_USDT("Фикс рублей продажа", "fix.rub.sell.usdt"),
    FIX_SELL_XMR("Фикс рублей продажа", "fix.rub.sell.xmr"),

    FIX_COMMISSION_BUY("Фикс комиссия покупка", "commission.fix.buy"),
    FIX_COMMISSION_BUY_BTC("Фикс комиссия покупка", "commission.fix.buy.btc"),
    FIX_COMMISSION_BUY_LTC("Фикс комиссия покупка", "commission.fix.buy.ltc"),
    FIX_COMMISSION_BUY_USDT("Фикс комиссия покупка", "commission.fix.buy.usdt"),
    FIX_COMMISSION_BUY_XMR("Фикс комиссия покупка", "commission.fix.buy.xmr"),

    FIX_COMMISSION("Фикс комиссия продажа", "commission.fix.sell"),
    FIX_COMMISSION_SELL_BTC("Фикс комиссия продажа", "commission.fix.sell.btc"),
    FIX_COMMISSION_SELL_LTC("Фикс комиссия продажа", "commission.fix.sell.ltc"),
    FIX_COMMISSION_SELL_USDT("Фикс комиссия продажа", "commission.fix.sell.usdt"),
    FIX_COMMISSION_SELL_XMR("Фикс комиссия продажа", "commission.fix.sell.xmr"),

    COMMISSION_BUY("Комиссия покупка", "commission.main.buy"),
    COMMISSION_BUY_BTC("Комиссия покупка", "commission.main.buy.btc"),
    COMMISSION_BUY_LTC("Комиссия покупка", "commission.main.buy.ltc"),
    COMMISSION_BUY_USDT("Комиссия покупка", "commission.main.buy.usdt"),
    COMMISSION_BUY_XMR("Комиссия покупка", "commission.main.buy.xmr"),

    COMMISSION_SELL("Комиссия продажа", "commission.main.sell"),
    COMMISSION_SELL_BTC("Комиссия продажа", "commission.main.sell.btc"),
    COMMISSION_SELL_LTC("Комиссия продажа", "commission.main.sell.ltc"),
    COMMISSION_SELL_USDT("Комиссия продажа", "commission.main.sell.usdt"),
    COMMISSION_SELL_XMR("Комиссия продажа", "commission.main.sell.xmr"),
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
    WALLET_BTC("Кошелек BTC", "wallet.btc"),
    WALLET_LTC("Кошелек LTC", "wallet.ltc"),
    WALLET_USDT("Кошелек USDT", "wallet.usdt"),
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

    public static BotVariableType getFix(CryptoCurrency cryptoCurrency, DealType dealType) {
        switch (cryptoCurrency) {
            case BITCOIN:
                switch (dealType) {
                    case SELL:
                        return FIX_SELL_BTC;
                    case BUY:
                        return FIX_BUY_BTC;
                }
            case LITECOIN:
                switch (dealType) {
                    case SELL:
                        return FIX_SELL_LTC;
                    case BUY:
                        return FIX_BUY_LTC;
                }
            case USDT:
                switch (dealType) {
                    case SELL:
                        return FIX_SELL_USDT;
                    case BUY:
                        return FIX_BUY_USDT;
                }
            case MONERO:
                switch (dealType) {
                    case SELL:
                        return FIX_SELL_XMR;
                    case BUY:
                        return FIX_BUY_XMR;
                }
        }
        throw new BaseException("Не найден фикс");
    }

    public static BotVariableType getFixCommission(CryptoCurrency cryptoCurrency, DealType dealType) {
        switch (cryptoCurrency) {
            case BITCOIN:
                switch (dealType) {
                    case SELL:
                        return FIX_COMMISSION_SELL_BTC;
                    case BUY:
                        return FIX_COMMISSION_BUY_BTC;
                }
            case LITECOIN:
                switch (dealType) {
                    case SELL:
                        return FIX_COMMISSION_SELL_LTC;
                    case BUY:
                        return FIX_COMMISSION_BUY_LTC;
                }
            case USDT:
                switch (dealType) {
                    case SELL:
                        return FIX_COMMISSION_SELL_USDT;
                    case BUY:
                        return FIX_COMMISSION_BUY_USDT;
                }
            case MONERO:
                switch (dealType) {
                    case BUY:
                        return FIX_COMMISSION_SELL_XMR;
                    case SELL:
                        return FIX_COMMISSION_BUY_XMR;
                }
        }
        throw new BaseException("Не найден фикс");
    }

    public static BotVariableType getCommission(CryptoCurrency cryptoCurrency, DealType dealType) {
        switch (cryptoCurrency) {
            case BITCOIN:
                switch (dealType) {
                    case SELL:
                        return COMMISSION_SELL_BTC;
                    case BUY:
                        return COMMISSION_BUY_BTC;
                }
            case LITECOIN:
                switch (dealType) {
                    case SELL:
                        return COMMISSION_SELL_LTC;
                    case BUY:
                        return COMMISSION_BUY_LTC;
                }
            case USDT:
                switch (dealType) {
                    case SELL:
                        return COMMISSION_SELL_USDT;
                    case BUY:
                        return COMMISSION_BUY_USDT;
                }
            case MONERO:
                switch (dealType) {
                    case SELL:
                        return COMMISSION_SELL_XMR;
                    case BUY:
                        return COMMISSION_BUY_XMR;
                }
        }
        throw new BaseException("Не найден фикс");
    }

    public static BotVariableType getTransactionCommission(CryptoCurrency cryptoCurrency) {
        switch (cryptoCurrency) {
            case BITCOIN:
                return TRANSACTION_COMMISSION_BTC;
            case LITECOIN:
                return TRANSACTION_COMMISSION_LTC;
            case USDT:
                return TRANSACTION_COMMISSION_USDT;
            case MONERO:
                return TRANSACTION_COMMISSION_XMR;
        }
        throw new BaseException("Не найден фикс");
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