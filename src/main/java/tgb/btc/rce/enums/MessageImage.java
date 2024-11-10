package tgb.btc.rce.enums;

import lombok.Getter;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;

@Getter
public enum MessageImage {
    START("Стартовое сообщение.", "✅БОТ АВТО-ОБМЕНА\n\n—Моментальный перевод на ваш кошелёк.\n" +
            "—Минимальные комиссии.\n" +
            "—Максимально быстрые зачисления. \n" +
            "—Не принимаем оплаты от третьих лиц и мошенников\n" +
            "—Не принимаем грязную и санкционную криптовалюту\n" +
            "—Выплачиваем чистые рубли"),
    MAIN_MENU("Сообщение, отправляющее меню.", "✅Нажми \"Купить\" или \"Продать\" и выбери криптовалюту для расчёта.\uFE0F\n\n" +
            "\uD83C\uDFB0Чтобы получить возможность играть в \"Лотерею\", необходимо совершить сделку."),
    DRAWS("Сообщение, отправляющее меню розыгрышей.", "Розыгрыши от обменника\uD83E\uDD73"),
    ROULETTE("Сообщение рулетки.", "Рулетка."),
    CHOOSE_FIAT("Выбор фиата.", "Выберите валюту."),
    CHOOSE_CRYPTO_CURRENCY_BUY("Выбор криптовалюты для покупки.", "Выберите криптовалюту для покупки."),
    CHOOSE_CRYPTO_CURRENCY_SELL("Выбор криптовалюты для продажи.", "Выберите криптовалюту для продажи."),
    PAYMENT_TYPES_BUY("Выбор типа оплаты.", "Выберите способ оплаты:"),
    PAYMENT_TYPES_SELL("Выбор типа оплаты.", "Выберите способ получения перевода:"),
    REFERRAL("Реферальная программа.", "Реферальная программа."),
    CONTACTS("Контакты.", "Контакты."),
    BITCOIN_INPUT_WALLET("Ввод кошелька BTC.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s"),
    LITECOIN_INPUT_WALLET("Ввод кошелька LTC.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s"),
    USDT_INPUT_WALLET("Ввод кошелька USDT.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s"),
    MONERO_INPUT_WALLET("Ввод кошелька XMR.", "Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s"),
    SAVED_WALLET("Предложение использовать сохраненный кошелек.", "Вы можете использовать ваш сохраненный адрес: %"),
    FIAT_INPUT_DETAILS("Ввод реквизитов для продажи.", "Введите %s реквизиты, куда вы хотите получить %s%s."),
    MAX_AMOUNT("Сообщение, в случае обмена больше допустимой суммы.", "Вы выбрали большую сумму для обмена, для обмена такой суммы свяжитесь с оператором.");

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
