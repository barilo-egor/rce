package tgb.btc.rce.enums;

import lombok.Getter;

@Getter
public enum MessageImage {
    CHOOSE_FIAT("Выбор фиата.", "Выберите валюту."),
    CHOOSE_CRYPTO_CURRENCY("Выбор криптовалюты.", "Выберите криптовалюту."),
    PAYMENT_TYPES("Выбор типа оплаты.", "Выберите тип оплаты."),
    REFERRAL("Реферальная программа.", "Реферальная программа."),
    CONTACTS("Контакты.", "Контакты."),
    BITCOIN_INPUT_WALLET("Ввод кошелька BTC.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s."),
    LITECOIN_INPUT_WALLET("Ввод кошелька LTC.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s."),
    USDT_INPUT_WALLET("Ввод кошелька USDT.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s."),
    MONERO_INPUT_WALLET("Ввод кошелька XMR.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.");

    final String description;
    final String defaultMessage;

    MessageImage(String description, String defaultMessage) {
        this.description = description;
        this.defaultMessage = defaultMessage;
    }

}
