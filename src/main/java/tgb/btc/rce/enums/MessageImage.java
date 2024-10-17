package tgb.btc.rce.enums;

import lombok.Getter;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;

@Getter
public enum MessageImage {
    CHOOSE_FIAT("Выбор фиата.", "Выберите валюту."),
    CHOOSE_CRYPTO_CURRENCY_BUY("Выбор криптовалюты для покупки.", "Выберите криптовалюту для покупки."),
    CHOOSE_CRYPTO_CURRENCY_SELL("Выбор криптовалюты для продажи.", "Выберите криптовалюту для продажи."),
    PAYMENT_TYPES_BUY("Выберите способ оплаты:", "Выберите тип оплаты."),
    PAYMENT_TYPES_SELL("Выберите способ получения перевода:", "Выберите тип оплаты."),
    REFERRAL("Реферальная программа.", "Реферальная программа."),
    CONTACTS("Контакты.", "Контакты."),
    BITCOIN_INPUT_WALLET("Ввод кошелька BTC.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s"),
    LITECOIN_INPUT_WALLET("Ввод кошелька LTC.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s"),
    USDT_INPUT_WALLET("Ввод кошелька USDT.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s"),
    MONERO_INPUT_WALLET("Ввод кошелька XMR.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s"),
    SAVED_WALLET("Предложение использовать сохраненный кошелек.", "Вы можете использовать ваш сохраненный адрес:"),
    FIAT_INPUT_DETAILS("Ввод реквизитов для продажи.", "Введите %s реквизиты, куда вы хотите получить %s%s.");

    final String description;
    final String defaultMessage;

    MessageImage(String description, String defaultMessage) {
        this.description = description;
        this.defaultMessage = defaultMessage;
    }

    public static MessageImage getInputWallet(CryptoCurrency cryptoCurrency) {
        switch (cryptoCurrency) {
            case LITECOIN:
                return LITECOIN_INPUT_WALLET;
            case USDT:
                return USDT_INPUT_WALLET;
            case MONERO:
                return MONERO_INPUT_WALLET;
            default:
                return BITCOIN_INPUT_WALLET;
        }
    }
}
