package tgb.btc.rce.enums.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.btc.library.constants.enums.bot.UserRole;

import java.util.Set;

@Getter
@AllArgsConstructor
public enum TextCommand {
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
    TURNING_DELIVERY_TYPE("Вкл/выкл способов доставки", UserRole.ADMIN_ACCESS);

    private final String text;

    private final Set<UserRole> roles;

    public boolean hasAccess(UserRole role) {
        return getRoles().contains(role);
    }
}
