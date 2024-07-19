package tgb.btc.rce.enums;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.interfaces.ICommand;
import tgb.btc.rce.constants.BotStringConstants;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public enum Command implements ICommand {
    /*
     * CallbackQuery
     */

    START("/start", false, false, UserRole.USER_ACCESS),
    NONE("none", true, false, UserRole.USER_ACCESS),
    CHAT_ID("/chatid", true, false, UserRole.USER_ACCESS),

    /*
      Reply
     */

    /**
     * UTIL
     */
    BACK(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("BACK"), true, false, UserRole.USER_ACCESS),
    ADMIN_BACK("Назад", false, false, UserRole.OPERATOR_ACCESS),
    CANCEL("Отмена", false, false, UserRole.USER_ACCESS),
    SHARE_CONTACT("Поделиться контактом", false, false, UserRole.USER_ACCESS),
    BOT_OFFED("BOT_OFFED", true, false, UserRole.USER_ACCESS),
    INLINE_DELETE("❌ Закрыть", true, false, UserRole.USER_ACCESS),
    CAPTCHA("captcha", false, false, UserRole.USER_ACCESS),

    /**
     * HIDDEN
     */
    DELETE_USER("/deleteuser", false, true, UserRole.ADMIN_ACCESS),
    MAKE_ADMIN("/makeadmin", false, true, UserRole.ADMIN_ACCESS),
    MAKE_OPERATOR("/makeoperator", false, true, UserRole.ADMIN_ACCESS),
    MAKE_USER("/makeuser", false, true, UserRole.ADMIN_ACCESS),
    HELP("/help", false, false, UserRole.ADMIN_ACCESS),

    /**
     * MAIN
     */
    BUY_BITCOIN(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("BUY_BITCOIN"), false, false, UserRole.USER_ACCESS),
    SELL_BITCOIN(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("SELL_BITCOIN"), false, false, UserRole.USER_ACCESS),
    CABINET(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("CABINET"), true, false, UserRole.USER_ACCESS),
    CONTACTS(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("CONTACTS"), true, false, UserRole.USER_ACCESS),
    DRAWS(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("DRAWS"), true, false, UserRole.USER_ACCESS),
    REFERRAL(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("REFERRAL"), false, false, UserRole.USER_ACCESS),
    ADMIN_PANEL("Админ панель", true, false, UserRole.ADMIN_ACCESS),
    OPERATOR_PANEL("Панель оператора", true, false, UserRole.OPERATOR_ACCESS),

    /**
     * DRAWS
     */
    LOTTERY(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("LOTTERY"), false, false, UserRole.USER_ACCESS),
    ROULETTE(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("ROULETTE"), true, false, UserRole.USER_ACCESS),
    SLOT_REEL(PropertiesPath.SLOT_REEL_PROPERTIES.getString("SLOT_REEL"), false, false, UserRole.USER_ACCESS),
    DICE(PropertiesPath.DICE_PROPERTIES.getString("DICE"), false, false, UserRole.USER_ACCESS),
    RPS(PropertiesPath.RPS_PROPERTIES.getString("button.text"), false, false, UserRole.USER_ACCESS),

    /**
     * REFERRAL
     */
    WITHDRAWAL_OF_FUNDS("Вывод средств", false, false, UserRole.USER_ACCESS),
    SHOW_WITHDRAWAL_REQUEST("SHOW_WITHDRAWAL_REQUEST", false, false, UserRole.USER_ACCESS),
    SEND_LINK(PropertiesPath.BOT_PROPERTIES.getString("bot.link"), false, false, UserRole.USER_ACCESS),
    HIDE_WITHDRAWAL("Скрыть", false, false, UserRole.ADMIN_ACCESS),
    CHANGE_REFERRAL_BALANCE("Изменить", false, false, UserRole.ADMIN_ACCESS),
    DELETE_WITHDRAWAL_REQUEST("Удалить", false, false, UserRole.USER_ACCESS),

    /**
     * ADMIN PANEL
     */
    REQUESTS("Заявки", true, false, UserRole.ADMIN_ACCESS),
    SEND_MESSAGES("Отправка сообщений", true, false, UserRole.ADMIN_ACCESS),
    BAN_UNBAN("Бан/разбан", false, false, UserRole.ADMIN_ACCESS),
    BOT_SETTINGS("Настройки бота", true, false, UserRole.ADMIN_ACCESS),
    REPORTS("Отчеты", true, false, UserRole.ADMIN_ACCESS),
    EDIT_CONTACTS("Редактирование контактов", true, false, UserRole.ADMIN_ACCESS),
    USER_REFERRAL_BALANCE("Реф.баланс юзера", false, false, UserRole.ADMIN_ACCESS),
    TURNING_CURRENCY("Включение криптовалют", false, false, UserRole.ADMIN_ACCESS),
    DISCOUNTS("Скидки", true, false, UserRole.ADMIN_ACCESS),
    USERS("Пользователи", true, false, UserRole.ADMIN_ACCESS),
    QUIT_ADMIN_PANEL("Выйти", false, false, UserRole.OPERATOR_ACCESS),
    TURNING_DELIVERY_TYPE("Вкл/выкл способов доставки", false, false, UserRole.ADMIN_ACCESS),
    BACKUP_DB("/backupdb", false, true, UserRole.ADMIN_ACCESS),

    DEALS_COUNT("Кол-во возможных сделок", false, false, UserRole.ADMIN_ACCESS),

    /**
     * DISCOUNTS
     */
    RANK_DISCOUNT("Ранговая скидка(персональная)", false, false, UserRole.ADMIN_ACCESS),
    CHANGE_RANK_DISCOUNT("change_rank_discount", false, false, UserRole.ADMIN_ACCESS),
    PERSONAL_BUY_DISCOUNT("Персональная, покупка", false, false, UserRole.ADMIN_ACCESS),
    PERSONAL_SELL_DISCOUNT("Персональная, продажа", false, false, UserRole.ADMIN_ACCESS),
    REFERRAL_PERCENT("Процент реферала", false, false, UserRole.ADMIN_ACCESS),
    TURN_RANK_DISCOUNT("Ранговая скидка(для всех)", false, false, UserRole.ADMIN_ACCESS),
    TURNING_RANK_DISCOUNT("TURNING_RANK_DISCOUNT", false, false, UserRole.ADMIN_ACCESS),

    /**
     * TURNING CURRENCIES
     */
    TURN_ON_CURRENCY("turn_on_currency", false, false, UserRole.ADMIN_ACCESS),
    TURN_OFF_CURRENCY("turn_off_currency", false, false, UserRole.ADMIN_ACCESS),

    /**
     * EDIT CONTACTS
     */
    ADD_CONTACT("Добавить контакт", false, false, UserRole.ADMIN_ACCESS),
    DELETE_CONTACT("Удалить контакт", false, false, UserRole.ADMIN_ACCESS),

    /**
     * SEND MESSAGES
     */
    MAILING_LIST("Рассылка", false, false, UserRole.ADMIN_ACCESS),
    SEND_MESSAGE_TO_USER("Сообщение пользователю", false, false, UserRole.ADMIN_ACCESS),

    /**
     * BOT SETTINGS
     */
    CURRENT_DATA("Текущие данные", false, false, UserRole.ADMIN_ACCESS),
    ON_BOT("Вкл.бота", false, false, UserRole.ADMIN_ACCESS),
    OFF_BOT("Выкл.бота", false, false, UserRole.ADMIN_ACCESS),
    BOT_MESSAGES("Сообщения бота", false, false, UserRole.ADMIN_ACCESS),
    BOT_VARIABLES("Переменные бота", false, false, UserRole.ADMIN_ACCESS),
    SYSTEM_MESSAGES("Сис.сообщения", false, false, UserRole.ADMIN_ACCESS),
    PAYMENT_TYPES("Типы оплаты", false, false, UserRole.OPERATOR_ACCESS),

    /**
     * DEAL
     */
    DEAL("DEAL", false, false, UserRole.USER_ACCESS),
    PAID(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("PAID"), false, false, UserRole.USER_ACCESS),
    CANCEL_DEAL("Отменить заявку", false, false, UserRole.USER_ACCESS),
    DELETE_DEAL("Удалить", false, false, UserRole.USER_ACCESS),
    SHOW_DEAL("Показать", false, false, UserRole.OPERATOR_ACCESS),
    SHOW_API_DEAL("show_api_deal", false, false, UserRole.OPERATOR_ACCESS),
    DELETE_USER_DEAL("delete_deal", false, false, UserRole.OPERATOR_ACCESS),
    DELETE_DEAL_AND_BLOCK_USER("Удалить и заблокировать", false, false, UserRole.OPERATOR_ACCESS),
    CONFIRM_USER_DEAL("Подтвердить", false, false, UserRole.OPERATOR_ACCESS),
    ADDITIONAL_VERIFICATION("Доп.верификация", false, false, UserRole.OPERATOR_ACCESS),
    USER_ADDITIONAL_VERIFICATION("USER_ADDITIONAL_VERIFICATION", false, false, UserRole.USER_ACCESS),
    SHARE_REVIEW("Оставить", false, false, UserRole.USER_ACCESS),
    CHOOSING_FIAT_CURRENCY("CHOOSING_FIAT_CURRENCY", false, false, UserRole.USER_ACCESS),
    USE_PROMO(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("USE_PROMO"), false, false, UserRole.USER_ACCESS),
    DONT_USE_PROMO(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("DONT_USE_PROMO"), false, false, UserRole.USER_ACCESS),
    USE_SAVED_WALLET("USE_SAVED_WALLET", false, false, UserRole.USER_ACCESS),

    /**
     * REQUESTS
     */
    NEW_DEALS("Новые заявки", false, false, UserRole.OPERATOR_ACCESS),
    NEW_WITHDRAWALS("Вывод средств", false, false, UserRole.OPERATOR_ACCESS),
    NEW_REVIEWS("Новые отзывы", false, false, UserRole.OPERATOR_ACCESS),

    PUBLISH_REVIEW("Опубликовать", false, false, UserRole.OPERATOR_ACCESS),
    DELETE_REVIEW("Удалить", false, false, UserRole.OPERATOR_ACCESS),

    /**
     * REPORTS
     */
    USERS_REPORT("Отчет по пользователям", false, false, UserRole.ADMIN_ACCESS),
    USER_INFORMATION("Информация о пользователе", false, false, UserRole.ADMIN_ACCESS),
    USERS_DEALS_REPORT("Отчет сделки пользователей", false, false, UserRole.ADMIN_ACCESS),
    DEAL_REPORTS("Отчет по сделкам", false, false, UserRole.ADMIN_ACCESS),
    PARTNERS_REPORT("Отчет по партнерам", false, false, UserRole.ADMIN_ACCESS),
    CHECKS_FOR_DATE("Чеки по дате", false, false, UserRole.ADMIN_ACCESS),
    SEND_CHECKS_FOR_DATE("SEND_CHECKS_FOR_DATE", false, false, UserRole.ADMIN_ACCESS),
    LOTTERY_REPORT("Отчет по лотереи", false, false, UserRole.ADMIN_ACCESS),

    CHANNEL_POST("CHANNEL_POST", false, false, UserRole.USER_ACCESS),

    /**
     * RECEIPTS
     */
    RECEIPTS_CANCEL_DEAL("Отменить сделку", false, false, UserRole.USER_ACCESS),

    /**
     * PAYMENT TYPES
     */
    NEW_PAYMENT_TYPE("Создать тип оплаты", false, false, UserRole.OPERATOR_ACCESS),
    DELETE_PAYMENT_TYPE("Удалить тип оплаты", false, false, UserRole.OPERATOR_ACCESS),
    DELETING_PAYMENT_TYPE("DELETING_PAYMENT_TYPE", false, false, UserRole.OPERATOR_ACCESS),
    NEW_PAYMENT_TYPE_REQUISITE("Создать реквизит", false, false, UserRole.OPERATOR_ACCESS),
    DELETE_PAYMENT_TYPE_REQUISITE("Удалить реквизит", false, false, UserRole.OPERATOR_ACCESS),
    DELETING_PAYMENT_TYPE_REQUISITE("DELETING_PAYMENT_TYPE_REQUISITE", false, false, UserRole.OPERATOR_ACCESS),
    TURN_PAYMENT_TYPES("Включение типов оплат", false, false, UserRole.OPERATOR_ACCESS),
    TURNING_PAYMENT_TYPES("TURNING_PAYMENT_TYPES", false, false, UserRole.OPERATOR_ACCESS),
    CHANGE_MIN_SUM("Мин.сумма", false, false, UserRole.OPERATOR_ACCESS),
    TURN_DYNAMIC_REQUISITES("Динамические реквизиты", false, false, UserRole.OPERATOR_ACCESS),
    TURNING_DYNAMIC_REQUISITES("TURNING_DYNAMIC_REQUISITES", false, false, UserRole.OPERATOR_ACCESS),

    /**
     * ANTISPAM
     */
    SHOW_SPAM_BANNED_USER("Показать", false, false, UserRole.OPERATOR_ACCESS),
    KEEP_SPAM_BAN("Оставить в бане", false, false, UserRole.OPERATOR_ACCESS),
    SPAM_UNBAN("Разблокировать", false, false, UserRole.OPERATOR_ACCESS),
    NEW_SPAM_BANS("Антиспам блоки", false, false, UserRole.OPERATOR_ACCESS),

    /**
     * USERS STATES
     */
    NONE_CALCULATOR("NONE_CALCULATOR", false, false, UserRole.USER_ACCESS),
    INLINE_QUERY_CALCULATOR("INLINE_QUERY_CALCULATOR", false, false, UserRole.USER_ACCESS),
    INLINE_CALCULATOR("INLINE_CALCULATOR", false, false, UserRole.USER_ACCESS),

    WEB_ADMIN_PANEL("Веб админ-панель", false, false, UserRole.OPERATOR_ACCESS),

    /**
     * API DEALS
     */
    CONFIRM_API_DEAL("Подтвердить", false, false, UserRole.OPERATOR_ACCESS),
    CANCEL_API_DEAL("Отклонить", false, false, UserRole.OPERATOR_ACCESS),
    NEW_API_DEALS("Новые API заявки", false, false, UserRole.OPERATOR_ACCESS),

    /**
     * TURNING DELIVERY
     */
    TURN_PROCESS_DELIVERY("TURN_PROCESS_DELIVERY", false, false, UserRole.ADMIN_ACCESS),

    /**
     * WEB
     */
    SUBMIT_LOGIN("Подтвердить вход", false, false, UserRole.USER_ACCESS),
    SUBMIT_REGISTER("Подтвердить регистрацию", false, false, UserRole.USER_ACCESS),
    LOGOUT("Закрыть сессию", false, false, UserRole.USER_ACCESS);

    public static final Set<Command> HIDDEN_COMMANDS = Arrays.stream(Command.values())
            .filter(Command::isHidden)
            .collect(Collectors.toSet());

    final String text;
    final boolean isSimple;
    final boolean isHidden;
    final Set<UserRole> roles;

    Command(String text, boolean isSimple, boolean isHidden, Set<UserRole> roles) {
        this.text = text;
        this.isSimple = isSimple;
        this.isHidden = isHidden;
        this.roles = roles;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public String getText() {
        return text;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public boolean hasAccess(UserRole role) {
        return this.getRoles().contains(role);
    }

    public boolean isHidden() {
        return isHidden;
    }

    public static Command fromUpdate(Update update) {
        switch (UpdateType.fromUpdate(update)) {
            case MESSAGE:
                return findByTextOrName(update.getMessage().getText());
            case CALLBACK_QUERY:
                return fromCallbackQuery(update.getCallbackQuery().getData());
            case INLINE_QUERY:
                return findByTextOrName(update.getInlineQuery().getQuery());
            case CHANNEL_POST:
                return Command.CHANNEL_POST;
            default:
                return Command.START;
        }
    }

    public static Command fromCallbackQuery(String value) {
        if (value.contains(BotStringConstants.CALLBACK_DATA_SPLITTER)) {
            value = value.split(BotStringConstants.CALLBACK_DATA_SPLITTER)[0];
        }
        try {
            return Command.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Command findByTextOrName(String value) {
        return Arrays.stream(Command.values())
                .filter(command -> (Objects.nonNull(command.getText()) && value.startsWith(command.getText())) || value.startsWith(command.name()))
                .findFirst()
                .orElse(null);
    }
}
