package tgb.btc.rce.constants;

import tgb.btc.rce.enums.WebProperties;

public interface BotStringConstants {
    String WRITE_TO_OPERATOR_BUTTON_LABEL = "Написать оператору";

    String CALLBACK_DATA_SPLITTER = ":";

    String SHOW_BUTTON = "Показать";

    String DEAL_INFO = "Заявка на %s №%s\n" + "Дата,время: %s\n" + "Тип оплаты: %s\n" + "Кошелек: %s\n" + "Контакт: %s\n"
            + "Количество сделок: %s\n" + "ID: %s\n" + "Сумма %s: %s\n" + "Сумма: %s %s";

    String ABSENT = "Отсутствует";

    String BUY_OR_SELL = "Покупка или продажа?";

    String FIAT_CURRENCY_CHOOSE = "Выберите фиатную валюту.";

    String USE_SAVED_WALLET = "use_saved";

    String USE_PROMO = "use_promo";

    String DONT_USE_PROMO = "dont_use_promo";

    String USE_REFERRAL_DISCOUNT = "use_discount";

    String DONT_USE_REFERRAL_DISCOUNT = "dont_use_discount";

    String MAIN_URL = WebProperties.SERVER.getString("main.url");
}
