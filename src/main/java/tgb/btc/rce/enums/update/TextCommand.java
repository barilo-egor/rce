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
    OBSERVER_PANEL("Панель наблюдателя");

    private final String text;
}
