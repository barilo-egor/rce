package tgb.btc.rce.enums.update;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TextCommand {
    BUY_BITCOIN("Купить"),
    SELL_BITCOIN("Продать"),
    CONTACTS("Контакты"),
    DRAWS("Розыгрыши"),
    REFERRAL("Реферальная программа"),
    ADMIN_PANEL("Админ панель"),
    WEB_ADMIN_PANEL("Веб админ-панель"),
    LOTTERY("Лотерея"),
    ROULETTE("Рулетка"),
    BOT_SETTINGS("Настройки бота"),
    DISCOUNTS("Скидки"),
    EDIT_CONTACTS("Редактирование контактов"),
    OBSERVER_PANEL("Панель наблюдателя"),
    OPERATOR_PANEL("Панель оператора"),
    REPORTS("Отчеты"),
    REQUESTS("Заявки"),
    SEND_MESSAGES("Отправка сообщений"),
    USERS("Пользователи"),
    NEW_API_DEALS("Новые API заявки"),
    NEW_DEALS("Новые заявки"),
    NEW_REVIEWS("Новые отзывы"),
    NEW_SPAM_BANS("Антиспам блоки"),
    NEW_WITHDRAWALS("Заявки на вывод"),
    ON_BOT("Вкл.бота"),
    OFF_BOT("Выкл.бота"),
    LOTTERY_REPORT("Отчет по лотереи");

    private final String text;
}
