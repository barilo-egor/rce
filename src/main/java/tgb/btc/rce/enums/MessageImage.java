package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;

@Getter
@AllArgsConstructor
public enum MessageImage {
    START("Стартовое сообщение.", """
            ✅БОТ АВТО-ОБМЕНА
            
            —Моментальный перевод на ваш кошелёк.
            —Минимальные комиссии.
            —Максимально быстрые зачисления.\s
            —Не принимаем оплаты от третьих лиц и мошенников
            —Не принимаем грязную и санкционную криптовалюту
            —Выплачиваем чистые рубли"""),
    MAIN_MENU("Сообщение, отправляющее меню.", """
            ✅Нажми "Купить" или "Продать" и выбери криптовалюту для расчёта.️
            
            \uD83C\uDFB0Чтобы получить возможность играть в "Лотерею", необходимо совершить сделку."""),
    DRAWS("Сообщение, отправляющее меню розыгрышей.", "Розыгрыши от обменника\uD83E\uDD73"),
    ROULETTE("Сообщение рулетки.", "Рулетка."),
    CHOOSE_FIAT("Выбор фиата.", "Выберите валюту."),
    CHOOSE_CRYPTO_CURRENCY_BUY("Выбор криптовалюты для покупки.", "Выберите криптовалюту для покупки."),
    CHOOSE_CRYPTO_CURRENCY_SELL("Выбор криптовалюты для продажи.", "Выберите криптовалюту для продажи."),
    PAYMENT_TYPES_BUY("Выбор типа оплаты.", "Выберите способ оплаты:"),
    PAYMENT_TYPES_SELL("Выбор типа оплаты.", "Выберите способ получения перевода:"),
    REFERRAL("Реферальная программа.", "Реферальная программа."),
    CONTACTS("Контакты.", "Контакты."),
    BITCOIN_INPUT_WALLET("Ввод кошелька BTC.", BotSystemMessage.INPUT_WALLET.getText()),
    LITECOIN_INPUT_WALLET("Ввод кошелька LTC.", BotSystemMessage.INPUT_WALLET.getText()),
    USDT_INPUT_WALLET("Ввод кошелька USDT.", BotSystemMessage.INPUT_WALLET.getText()),
    MONERO_INPUT_WALLET("Ввод кошелька XMR.", BotSystemMessage.INPUT_WALLET.getText()),
    SAVED_WALLET("Предложение использовать сохраненный кошелек.", "Вы можете использовать ваш сохраненный адрес: %"),
    FIAT_INPUT_DETAILS("Ввод реквизитов для продажи.", "Введите %s реквизиты, куда вы хотите получить %s%s."),
    MAX_AMOUNT("Сообщение, в случае обмена больше допустимой суммы.", "Вы выбрали большую сумму для обмена, для обмена такой суммы свяжитесь с оператором."),
    WON_LOTTERY("Сообщение при выигрыше лотереи.", "Ты победил!"),
    LOSE_LOTTERY("Сообщение при проигрыше лотереи.", "К сожалению, ты проиграл."),
    BOT_OFF("Сообщение при выключенном боте.", "Приносим свои извинения, проводятся технические работы.");

    final String description;
    final String defaultMessage;

    public static MessageImage getInputWallet(CryptoCurrency cryptoCurrency) {
        return switch (cryptoCurrency) {
            case LITECOIN -> LITECOIN_INPUT_WALLET;
            case USDT -> USDT_INPUT_WALLET;
            case MONERO -> MONERO_INPUT_WALLET;
            case BITCOIN -> BITCOIN_INPUT_WALLET;
        };
    }
}
