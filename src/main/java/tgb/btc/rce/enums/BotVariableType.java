package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

public enum BotVariableType {
    USD_COURSE("Курс доллара", "course.usd"),
    FIX_BUY_BTC("Фикс рублей", "fix.rub.buy.btc"),
    FIX_BUY_LTC("Фикс рублей", "fix.rub.buy.ltc"),
    FIX_BUY_USDT("Фикс рублей", "fix.rub.buy.usdt"),
    FIX_SELL_BTC("Фикс рублей", "fix.rub.sell.btc"),
    FIX_SELL_LTC("Фикс рублей", "fix.rub.sell.ltc"),
    FIX_SELL_USDT("Фикс рублей", "fix.rub.sell.usdt"),
    FIX_COMMISSION_BUY_BTC("Фикс комиссия", "commission.fix.buy.btc"),
    FIX_COMMISSION_BUY_LTC("Фикс комиссия", "commission.fix.buy.ltc"),
    FIX_COMMISSION_BUY_USDT("Фикс комиссия", "commission.fix.buy.usdt"),
    FIX_COMMISSION_SELL_BTC("Фикс комиссия", "commission.fix.sell.btc"),
    FIX_COMMISSION_SELL_LTC("Фикс комиссия", "commission.fix.sell.ltc"),
    FIX_COMMISSION_SELL_USDT("Фикс комиссия", "commission.fix.sell.usdt"),
    COMMISSION_BUY_BTC("Комиссия", "commission.main.buy.btc"),
    COMMISSION_BUY_LTC("Комиссия", "commission.main.buy.ltc"),
    COMMISSION_BUY_USDT("Комиссия", "commission.main.buy.usdt"),
    COMMISSION_SELL_BTC("Комиссия", "commission.main.sell.btc"),
    COMMISSION_SELL_LTC("Комиссия", "commission.main.sell.ltc"),
    COMMISSION_SELL_USDT("Комиссия", "commission.main.sell.usdt"),
    OPERATOR_LINK("Ссылка на оператора", "operator.link"),
    PROBABILITY("Шанс лотереи", "lottery.chance"),
    MIN_SUM_BUY_BTC("Мин.сумма BTC", "deal.buy.sum.min.btc"),
    MIN_SUM_BUY_LTC("Мин.сумма", "deal.buy.sum.min.ltc"),
    MIN_SUM_BUY_USDT("Мин.сумма", "deal.buy.sum.min.usdt"),
    TRANSACTION_COMMISSION("Транз.комиссия", "transaction.commission"),
    TRANSACTION_COMMISSION_BTC("Транз.комиссия", "transaction.commission.btc"),
    TRANSACTION_COMMISSION_LTC("Транз.комиссия", "transaction.commission.ltc"),
    TRANSACTION_COMMISSION_USDT("Транз.комиссия", "transaction.commission.usdt"),
    PROMO_CODE_DISCOUNT("Скидка от промокода", "promo.code.discount"),
    PROMO_CODE_NAME("Название промокода", "promo.code.name"),
    DEAL_ACTIVE_TIME("Время активности заявки", "deal.active.time"),
    WALLET_BTC("Кошелек BTC", "wallet.btc"),
    WALLET_LTC("Кошелек LTC", "wallet.ltc"),
    WALLET_USDT("Кошелек USDT", "wallet.usdt"),
    REFERRAL_PERCENT("Процент рефералов", "referral.percent"),
    REFERRAL_MIN_SUM("Мин.сумма вывода", "referral.min.sum"),
    CHANNEL_CHAT_ID("Айди канала", "channel.chat.id"),
    REVIEW_PRISE("Вознаграждение", "review.prise");

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