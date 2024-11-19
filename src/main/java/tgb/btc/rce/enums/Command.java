package tgb.btc.rce.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.interfaces.ICommand;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Getter
public enum Command implements ICommand {
    /*
     * CallbackQuery
     */

    START("/start", false, UserRole.USER_ACCESS), // +
    NONE("none", false, UserRole.USER_ACCESS),
    CHAT_ID("/chatid", false, UserRole.USER_ACCESS), // +

    /*
      Reply
     */

    /**
     * UTIL
     */
    BACK(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS), // +
    ADMIN_BACK("Назад", false, UserRole.OPERATOR_ACCESS),
    CANCEL("Отмена", false, UserRole.USER_ACCESS),
    SHARE_CONTACT("Поделиться контактом", false, UserRole.USER_ACCESS),
    BOT_OFFED("BOT_OFFED", false, UserRole.USER_ACCESS),
    INLINE_DELETE("❌ Закрыть", false, UserRole.USER_ACCESS),
    CAPTCHA("captcha", false, UserRole.USER_ACCESS),

    /**
     * HIDDEN
     */
    DELETE_USER("/deleteuser", true, UserRole.ADMIN_ACCESS),// +
    MAKE_ADMIN("/makeadmin", true, UserRole.ADMIN_ACCESS),// +
    MAKE_OPERATOR("/makeoperator", true, UserRole.ADMIN_ACCESS),// +
    MAKE_OBSERVER("/makeobserver", true, UserRole.ADMIN_ACCESS),// +
    MAKE_USER("/makeuser", true, UserRole.ADMIN_ACCESS),// +
    HELP("/help", false, UserRole.ADMIN_ACCESS),// +
    TURN_NOTIFICATIONS("/notifications", true, UserRole.OBSERVER_ACCESS),// +

    /**
     * MAIN
     */
    BUY_BITCOIN(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),// +
    SELL_BITCOIN(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),// +
    CABINET(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),
    CONTACTS(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),// +
    DRAWS(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),// +
    REFERRAL(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),// +
    ADMIN_PANEL("Админ панель", false, UserRole.ADMIN_ACCESS),// +
    OPERATOR_PANEL("Панель оператора", false, UserRole.OPERATOR_ACCESS),// +
    OBSERVER_PANEL("Панель наблюдателя", false, UserRole.OBSERVER_ACCESS),// +

    /**
     * DRAWS
     */
    LOTTERY(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),// +
    ROULETTE(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),// +
    SLOT_REEL(PropertiesPath.SLOT_REEL_PROPERTIES.name(), false, UserRole.USER_ACCESS),
    DICE(PropertiesPath.DICE_PROPERTIES.name(), false, UserRole.USER_ACCESS),
    RPS(PropertiesPath.RPS_PROPERTIES.name(), false, UserRole.USER_ACCESS),

    /**
     * REFERRAL
     */
    WITHDRAWAL_OF_FUNDS("Вывод средств", false, UserRole.USER_ACCESS),
    SHOW_WITHDRAWAL_REQUEST("SHOW_WITHDRAWAL_REQUEST", false, UserRole.USER_ACCESS),
    HIDE_WITHDRAWAL("Скрыть", false, UserRole.ADMIN_ACCESS),
    CHANGE_REFERRAL_BALANCE("Изменить", false, UserRole.ADMIN_ACCESS),
    DELETE_WITHDRAWAL_REQUEST("Удалить", false, UserRole.USER_ACCESS),

    /**
     * ADMIN PANEL
     */
    REQUESTS("Заявки", false, UserRole.ADMIN_ACCESS),// +
    SEND_MESSAGES("Отправка сообщений", false, UserRole.ADMIN_ACCESS),// +
    BAN_UNBAN("Бан/разбан", false, UserRole.ADMIN_ACCESS),
    BOT_SETTINGS("Настройки бота", false, UserRole.ADMIN_ACCESS),// +
    REPORTS("Отчеты", false, UserRole.ADMIN_ACCESS),// +
    EDIT_CONTACTS("Редактирование контактов", false, UserRole.ADMIN_ACCESS),// +
    USER_REFERRAL_BALANCE("Реф.баланс юзера", false, UserRole.ADMIN_ACCESS),
    TURNING_CURRENCY("Включение криптовалют", false, UserRole.ADMIN_ACCESS),// +
    DISCOUNTS("Скидки", false, UserRole.ADMIN_ACCESS),// +
    USERS("Пользователи", false, UserRole.ADMIN_ACCESS),// +
    QUIT_ADMIN_PANEL("Выйти", false, UserRole.OBSERVER_ACCESS),// +
    TURNING_DELIVERY_TYPE("Вкл/выкл способов доставки", false, UserRole.ADMIN_ACCESS),// +
    BACKUP_DB("/backupdb", true, UserRole.ADMIN_ACCESS),// +

    DEALS_COUNT("Кол-во возможных сделок", false, UserRole.ADMIN_ACCESS),

    /**
     * DISCOUNTS
     */
    RANK_DISCOUNT("Ранговая скидка(персональная)", false, UserRole.ADMIN_ACCESS),
    CHANGE_RANK_DISCOUNT("change_rank_discount", false, UserRole.ADMIN_ACCESS),
    PERSONAL_BUY_DISCOUNT("Персональная, покупка", false, UserRole.ADMIN_ACCESS),
    PERSONAL_SELL_DISCOUNT("Персональная, продажа", false, UserRole.ADMIN_ACCESS),
    REFERRAL_PERCENT("Процент реферала", false, UserRole.ADMIN_ACCESS),
    TURN_RANK_DISCOUNT("Ранговая скидка(для всех)", false, UserRole.ADMIN_ACCESS), //+
    TURNING_RANK_DISCOUNT("TURNING_RANK_DISCOUNT", false, UserRole.ADMIN_ACCESS),

    /**
     * TURNING CURRENCIES
     */
    TURN_ON_CURRENCY("turn_on_currency", false, UserRole.ADMIN_ACCESS),
    TURN_OFF_CURRENCY("turn_off_currency", false, UserRole.ADMIN_ACCESS),

    /**
     * EDIT CONTACTS
     */
    ADD_CONTACT("Добавить контакт", false, UserRole.ADMIN_ACCESS),
    DELETE_CONTACT("Удалить контакт", false, UserRole.ADMIN_ACCESS),

    /**
     * SEND MESSAGES
     */
    MAILING_LIST("Рассылка", false, UserRole.OBSERVER_ACCESS),
    SEND_MESSAGE_TO_USER("Сообщение пользователю", false, UserRole.ADMIN_ACCESS),

    /**
     * BOT SETTINGS
     */
    CURRENT_DATA("Текущие данные", false, UserRole.ADMIN_ACCESS),
    ON_BOT("Вкл.бота", false, UserRole.ADMIN_ACCESS),
    OFF_BOT("Выкл.бота", false, UserRole.ADMIN_ACCESS),
    BOT_MESSAGES("Сообщения бота", false, UserRole.ADMIN_ACCESS),
    BOT_VARIABLES("Переменные бота", false, UserRole.ADMIN_ACCESS),
    SYSTEM_MESSAGES("Сис.сообщения", false, UserRole.ADMIN_ACCESS),
    PAYMENT_TYPES("Типы оплаты", false, UserRole.OPERATOR_ACCESS),

    /**
     * DEAL
     */
    DEAL("DEAL", false, UserRole.USER_ACCESS),
    PAID(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),
    CANCEL_DEAL("Отменить заявку", false, UserRole.USER_ACCESS),
    DELETE_DEAL("Удалить", false, UserRole.USER_ACCESS),
    SHOW_DEAL("Показать", false, UserRole.OBSERVER_ACCESS),
    SHOW_API_DEAL("show_api_deal", false, UserRole.OPERATOR_ACCESS),
    DELETE_USER_DEAL("Удалить", false, UserRole.OPERATOR_ACCESS),
    DELETE_DEAL_AND_BLOCK_USER("Удалить и заблокировать", false, UserRole.OPERATOR_ACCESS),
    CONFIRM_USER_DEAL("Подтвердить", false, UserRole.OPERATOR_ACCESS),
    AUTO_WITHDRAWAL_DEAL("Автовывод", false, UserRole.OPERATOR_ACCESS),
    BITCOIN_POOL_WITHDRAWAL("Автовывод", false, UserRole.OPERATOR_ACCESS),
    CONFIRM_POOL_WITHDRAWAL("Да", false, UserRole.OPERATOR_ACCESS),
    ADD_TO_POOL("Добавить в пул", false, UserRole.OPERATOR_ACCESS),
    CLEAR_POOL("Очистить пул", false, UserRole.OPERATOR_ACCESS),
    CONFIRM_CLEAR_POOL("Да", false, UserRole.OPERATOR_ACCESS),
    DELETE_FROM_POOL("/deletefrompool", true, UserRole.OPERATOR_ACCESS),
    CONFIRM_AUTO_WITHDRAWAL_DEAL("Продолжить", false, UserRole.OPERATOR_ACCESS),
    ADDITIONAL_VERIFICATION("Доп.верификация", false, UserRole.OPERATOR_ACCESS),
    USER_ADDITIONAL_VERIFICATION("USER_ADDITIONAL_VERIFICATION", false, UserRole.USER_ACCESS),
    SHARE_REVIEW("Оставить", false, UserRole.USER_ACCESS),
    CHOOSING_FIAT_CURRENCY("CHOOSING_FIAT_CURRENCY", false, UserRole.USER_ACCESS),
    USE_PROMO(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),
    DONT_USE_PROMO(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.name(), false, UserRole.USER_ACCESS),
    USE_SAVED_WALLET("USE_SAVED_WALLET", false, UserRole.USER_ACCESS),

    /**
     * REQUESTS
     */
    NEW_DEALS("Новые заявки", false, UserRole.OBSERVER_ACCESS),
    BITCOIN_POOL("Пул сделок BTC", false, UserRole.OPERATOR_ACCESS),
    NEW_WITHDRAWALS("Заявки на вывод", false, UserRole.OPERATOR_ACCESS),
    NEW_REVIEWS("Новые отзывы", false, UserRole.OPERATOR_ACCESS),
    REVIEW_NAVIGATION("REVIEW_NAVIGATION", false, UserRole.OPERATOR_ACCESS),

    PUBLISH_REVIEW("Опубликовать", false, UserRole.OPERATOR_ACCESS),
    DELETE_REVIEW("Удалить", false, UserRole.OPERATOR_ACCESS),

    /**
     * REPORTS
     */
    USERS_REPORT("Отчет по пользователям", false, UserRole.ADMIN_ACCESS),
    USER_INFORMATION("Информация о пользователе", false, UserRole.ADMIN_ACCESS),
    USERS_DEALS_REPORT("Отчет сделки пользователей", false, UserRole.ADMIN_ACCESS),
    DEAL_REPORTS("Отчет по сделкам", false, Set.of(UserRole.OBSERVER, UserRole.ADMIN)),
    PARTNERS_REPORT("Отчет по партнерам", false, UserRole.ADMIN_ACCESS),
    CHECKS_FOR_DATE("Чеки по дате", false, UserRole.ADMIN_ACCESS),
    SEND_CHECKS_FOR_DATE("SEND_CHECKS_FOR_DATE", false, UserRole.ADMIN_ACCESS),
    LOTTERY_REPORT("Отчет по лотереи", false, UserRole.ADMIN_ACCESS),

    CHANNEL_POST("CHANNEL_POST", false, UserRole.USER_ACCESS),

    /**
     * RECEIPTS
     */
    RECEIPTS_CANCEL_DEAL("Отменить сделку", false, UserRole.USER_ACCESS),

    /**
     * PAYMENT TYPES
     */
    NEW_PAYMENT_TYPE("Создать тип оплаты", false, UserRole.OPERATOR_ACCESS),
    DELETE_PAYMENT_TYPE("Удалить тип оплаты", false, UserRole.OPERATOR_ACCESS),
    DELETING_PAYMENT_TYPE("DELETING_PAYMENT_TYPE", false, UserRole.OPERATOR_ACCESS),
    NEW_PAYMENT_TYPE_REQUISITE("Создать реквизит", false, UserRole.OPERATOR_ACCESS),
    DELETE_PAYMENT_TYPE_REQUISITE("Удалить реквизит", false, UserRole.OPERATOR_ACCESS),
    DELETING_PAYMENT_TYPE_REQUISITE("DELETING_PAYMENT_TYPE_REQUISITE", false, UserRole.OPERATOR_ACCESS),
    TURN_PAYMENT_TYPES("Включение типов оплат", false, UserRole.OPERATOR_ACCESS),
    TURNING_PAYMENT_TYPES("TURNING_PAYMENT_TYPES", false, UserRole.OPERATOR_ACCESS),
    CHANGE_MIN_SUM("Мин.сумма", false, UserRole.OPERATOR_ACCESS),
    TURN_DYNAMIC_REQUISITES("Динамические реквизиты", false, UserRole.OPERATOR_ACCESS),
    TURNING_DYNAMIC_REQUISITES("TURNING_DYNAMIC_REQUISITES", false, UserRole.OPERATOR_ACCESS),

    /**
     * ANTISPAM
     */
    SHOW_SPAM_BANNED_USER("Показать", false, UserRole.OPERATOR_ACCESS),
    KEEP_SPAM_BAN("Оставить в бане", false, UserRole.OPERATOR_ACCESS),
    SPAM_UNBAN("Разблокировать", false, UserRole.OPERATOR_ACCESS),
    NEW_SPAM_BANS("Антиспам блоки", false, UserRole.OPERATOR_ACCESS),

    /**
     * USERS STATES
     */
    NONE_CALCULATOR("NONE_CALCULATOR", false, UserRole.USER_ACCESS),
    INLINE_QUERY_CALCULATOR("INLINE_QUERY_CALCULATOR", false, UserRole.USER_ACCESS),
    INLINE_CALCULATOR("INLINE_CALCULATOR", false, UserRole.USER_ACCESS),

    WEB_ADMIN_PANEL("Веб админ-панель", false, UserRole.OPERATOR_ACCESS),

    /**
     * API DEALS
     */
    CONFIRM_API_DEAL("Подтвердить", false, UserRole.OPERATOR_ACCESS),
    CANCEL_API_DEAL("Отклонить", false, UserRole.OPERATOR_ACCESS),
    NEW_API_DEALS("Новые API заявки", false, UserRole.OPERATOR_ACCESS),

    /**
     * TURNING DELIVERY
     */
    TURN_PROCESS_DELIVERY("TURN_PROCESS_DELIVERY", false, UserRole.ADMIN_ACCESS),

    /**
     * WEB
     */
    SUBMIT_LOGIN("Подтвердить вход", false, UserRole.USER_ACCESS),
    SUBMIT_REGISTER("Подтвердить регистрацию", false, UserRole.USER_ACCESS),
    LOGOUT("Закрыть сессию", false, UserRole.USER_ACCESS);

    public static final Set<Command> HIDDEN_COMMANDS = Arrays.stream(Command.values())
            .filter(Command::isHidden)
            .collect(Collectors.toSet());

    public static final List<Command> NEW_HANDLE = List.of(
            START, HELP, CHAT_ID, MAKE_USER, MAKE_OBSERVER, MAKE_OPERATOR, MAKE_ADMIN, BACKUP_DB, DELETE_FROM_POOL, DELETE_USER, TURN_NOTIFICATIONS,
            BUY_BITCOIN, SELL_BITCOIN, CONTACTS, DRAWS, REFERRAL, ADMIN_PANEL, WEB_ADMIN_PANEL, LOTTERY, ROULETTE, BOT_SETTINGS,
            DISCOUNTS, EDIT_CONTACTS, OBSERVER_PANEL, OPERATOR_PANEL, REPORTS, REQUESTS, SEND_MESSAGES, USERS, NEW_API_DEALS,
            NEW_DEALS, NEW_REVIEWS, NEW_SPAM_BANS, NEW_WITHDRAWALS, ON_BOT, OFF_BOT, LOTTERY_REPORT, PARTNERS_REPORT, BACK,
            TURNING_CURRENCY, QUIT_ADMIN_PANEL, TURNING_DELIVERY_TYPE, TURN_RANK_DISCOUNT
    );

    final String text;
    final boolean isHidden;
    final Set<UserRole> roles;

    Command(String text, boolean isHidden, Set<UserRole> roles) {
        this.text = text;
        this.isHidden = isHidden;
        this.roles = roles;
    }

    public boolean hasAccess(UserRole role) {
        return this.getRoles().contains(role);
    }
}
