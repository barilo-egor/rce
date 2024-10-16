package tgb.btc.rce.enums;

public enum MessageImage {
    CHOOSE_FIAT("Выбор фиата."),
    CHOOSE_CRYPTO_CURRENCY("Выбор криптовалюты."),
    PAYMENT_TYPES("Выбор типа оплаты."),
    REFERRAL("Реферальная программа."),
    CONTACTS("Контакты."),
    BITCOIN_INPUT_WALLET("Ввод кошелька BTC."),
    LITECOIN_INPUT_WALLET("Ввод кошелька LTC."),
    USDT_INPUT_WALLET("Ввод кошелька USDT."),
    MONERO_INPUT_WALLET("Ввод кошелька XMR."),
    RUB_INPUT_WALLET("Ввод кошелька RUB.");

    final String description;

    MessageImage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
