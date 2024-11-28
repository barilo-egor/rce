package tgb.btc.rce.enums.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.library.constants.enums.bot.UserRole;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum TextCommand {
    CANCEL("Отмена", UserRole.USER_ACCESS),
    BACK("Назад", UserRole.USER_ACCESS),
    BUY_BITCOIN("Купить", UserRole.USER_ACCESS),
    SELL_BITCOIN("Продать", UserRole.USER_ACCESS),
    CONTACTS("Контакты", UserRole.USER_ACCESS),
    DRAWS("Розыгрыши", UserRole.USER_ACCESS),
    REFERRAL("Реферальная программа", UserRole.USER_ACCESS),
    ADMIN_PANEL("Админ панель", UserRole.ADMIN_ACCESS),
    WEB_ADMIN_PANEL("Веб админ-панель", UserRole.OPERATOR_ACCESS),
    LOTTERY("Лотерея", UserRole.USER_ACCESS),
    ROULETTE("Рулетка", UserRole.USER_ACCESS),
    BOT_SETTINGS("Настройки бота", UserRole.ADMIN_ACCESS),
    DISCOUNTS("Скидки", UserRole.ADMIN_ACCESS),
    EDIT_CONTACTS("Редактирование контактов", UserRole.ADMIN_ACCESS),
    OBSERVER_PANEL("Панель наблюдателя", UserRole.OBSERVER_ACCESS),
    OPERATOR_PANEL("Панель оператора", UserRole.OPERATOR_ACCESS),
    REPORTS("Отчеты", UserRole.ADMIN_ACCESS),
    REQUESTS("Заявки", UserRole.ADMIN_ACCESS),
    SEND_MESSAGES("Отправка сообщений", UserRole.ADMIN_ACCESS),
    USERS("Пользователи", UserRole.ADMIN_ACCESS),
    NEW_API_DEALS("Новые API заявки", UserRole.OPERATOR_ACCESS),
    NEW_DEALS("Новые заявки", UserRole.OBSERVER_ACCESS),
    NEW_REVIEWS("Новые отзывы", UserRole.OPERATOR_ACCESS),
    NEW_SPAM_BANS("Антиспам блоки", UserRole.OPERATOR_ACCESS),
    NEW_WITHDRAWALS("Заявки на вывод", UserRole.OPERATOR_ACCESS),
    ON_BOT("Вкл.бота", UserRole.ADMIN_ACCESS),
    OFF_BOT("Выкл.бота", UserRole.ADMIN_ACCESS),
    LOTTERY_REPORT("Отчет по лотереи", UserRole.ADMIN_ACCESS),
    PARTNERS_REPORT("Отчет по партнерам", UserRole.ADMIN_ACCESS),
    TURNING_CURRENCY("Включение криптовалют", UserRole.ADMIN_ACCESS),
    QUIT_ADMIN_PANEL("Выйти", UserRole.OBSERVER_ACCESS),
    TURNING_DELIVERY_TYPE("Вкл/выкл способов доставки", UserRole.ADMIN_ACCESS),
    TURN_RANK_DISCOUNT("Ранговая скидка(для всех)", UserRole.ADMIN_ACCESS),
    PAYMENT_TYPES("Типы оплаты", UserRole.OPERATOR_ACCESS),
    BITCOIN_POOL("Пул сделок BTC", UserRole.OPERATOR_ACCESS),
    USERS_REPORT("Отчет по пользователям", UserRole.ADMIN_ACCESS),
    USERS_DEALS_REPORT("Отчет сделки пользователей", UserRole.ADMIN_ACCESS),
    USER_REFERRAL_BALANCE("Реф.баланс юзера", UserRole.ADMIN_ACCESS),
    RANK_DISCOUNT("Ранговая скидка(персональная)", UserRole.ADMIN_ACCESS),
    DEALS_COUNT("Кол-во возможных сделок", UserRole.ADMIN_ACCESS),
    DEAL_REPORTS("Отчет по сделкам", Set.of(UserRole.OBSERVER, UserRole.ADMIN)),
    MAILING_LIST("Рассылка", UserRole.OBSERVER_ACCESS),
    REFERRAL_PERCENT("Процент реферала", UserRole.ADMIN_ACCESS),
    ADMIN_BACK("Назад", UserRole.OPERATOR_ACCESS),
    BAN_UNBAN("Бан/разбан", UserRole.ADMIN_ACCESS),
    USER_INFORMATION("Информация о пользователе", UserRole.ADMIN_ACCESS),
    ADD_CONTACT("Добавить контакт", UserRole.ADMIN_ACCESS),
    DELETE_CONTACT("Удалить контакт", UserRole.ADMIN_ACCESS),
    SEND_MESSAGE_TO_USER("Сообщение пользователю", UserRole.ADMIN_ACCESS),
    BOT_VARIABLES("Переменные бота", UserRole.ADMIN_ACCESS),
    CHECKS_FOR_DATE("Чеки по дате", UserRole.ADMIN_ACCESS),
    NEW_PAYMENT_TYPE("Создать тип оплаты", UserRole.OPERATOR_ACCESS),
    DELETE_PAYMENT_TYPE("Удалить тип оплаты", UserRole.OPERATOR_ACCESS),
    NEW_PAYMENT_TYPE_REQUISITE("Создать реквизит", UserRole.OPERATOR_ACCESS),
    DELETE_PAYMENT_TYPE_REQUISITE("Удалить реквизит", UserRole.OPERATOR_ACCESS),
    TURN_PAYMENT_TYPES("Включение типов оплат", UserRole.OPERATOR_ACCESS),
    CHANGE_MIN_SUM("Мин.сумма", UserRole.OPERATOR_ACCESS),
    TURN_DYNAMIC_REQUISITES("Динамические реквизиты", UserRole.OPERATOR_ACCESS),
    PERSONAL_BUY_DISCOUNT("Персональная, покупка", UserRole.ADMIN_ACCESS),
    PERSONAL_SELL_DISCOUNT("Персональная, продажа", UserRole.ADMIN_ACCESS),
    RECEIPTS_CANCEL_DEAL("Отменить сделку", UserRole.USER_ACCESS),
    ;

    private final String text;
    private final Set<UserRole> roles;

    public boolean hasAccess(UserRole role) {
        return getRoles().contains(role);
    }
}
