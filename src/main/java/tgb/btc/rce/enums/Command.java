package tgb.btc.rce.enums;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.interfaces.ICommand;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

@Slf4j
public enum Command implements ICommand {
    /*
     * CallbackQuery
     */

    START("/start", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    NONE("none", true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    CHAT_ID("/chatid", true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),

    /*
      Reply
     */

    /**
     * UTIL
     */
    BACK(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("BACK"), true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    ADMIN_BACK("Назад", false, false, Set.of(UserRole.ADMIN)),
    CANCEL("Отмена", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    SHARE_CONTACT("Поделиться контактом", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    BOT_OFFED("bot_offed", true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    INLINE_DELETE("inline_delete", true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    CAPTCHA("captcha", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),

    /**
     * HIDDEN
     */
    DELETE_USER("/deleteuser", false, true, Set.of(UserRole.ADMIN)),
    CREATE_USER_DATA("/createuserdata", false, true, Set.of(UserRole.ADMIN)),
    MAKE_ADMIN("/makeadmin", false, true, Set.of(UserRole.ADMIN)),
    HELP("/help", false, true, Set.of(UserRole.ADMIN)),

    /**
     * MAIN
     */
    BUY_BITCOIN(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("BUY_BITCOIN"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    SELL_BITCOIN(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("SELL_BITCOIN"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    CABINET(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("CABINET"), true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    CONTACTS(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("CONTACTS"), true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    DRAWS(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("DRAWS"), true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    REFERRAL(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("REFERRAL"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    ADMIN_PANEL("Админ панель", true, false, Set.of(UserRole.ADMIN)),

    /**
     * DRAWS
     */
    LOTTERY(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("LOTTERY"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    ROULETTE(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("ROULETTE"), true, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    SLOT_REEL(PropertiesPath.SLOT_REEL_PROPERTIES.getString("SLOT_REEL"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    DICE(PropertiesPath.DICE_PROPERTIES.getString("DICE"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    RPS(PropertiesPath.RPS_PROPERTIES.getString("button.text"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),

    /**
     * REFERRAL
     */
    WITHDRAWAL_OF_FUNDS("withdrawal", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    SHOW_WITHDRAWAL_REQUEST("show_withdrawal", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    SEND_LINK(PropertiesPath.BOT_PROPERTIES.getString("bot.link"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    HIDE_WITHDRAWAL("hide_withdrawal", false, false, Set.of(UserRole.ADMIN)),
    CHANGE_REFERRAL_BALANCE("change_ref", false, false, Set.of(UserRole.ADMIN)),
    DELETE_WITHDRAWAL_REQUEST("withdrawal_delete", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),

    /**
     * ADMIN PANEL
     */
    REQUESTS("Заявки", true, false, Set.of(UserRole.ADMIN)),
    SEND_MESSAGES("Отправка сообщений", true, false, Set.of(UserRole.ADMIN)),
    BAN_UNBAN("Бан/разбан", false, false, Set.of(UserRole.ADMIN)),
    BOT_SETTINGS("Настройки бота", true, false, Set.of(UserRole.ADMIN)),
    REPORTS("Отчеты", true, false, Set.of(UserRole.ADMIN)),
    EDIT_CONTACTS("Редактирование контактов", true, false, Set.of(UserRole.ADMIN)),
    USER_REFERRAL_BALANCE("Реф.баланс юзера", false, false, Set.of(UserRole.ADMIN)),
    TURNING_CURRENCY("Включение криптовалют", false, false, Set.of(UserRole.ADMIN)),
    DISCOUNTS("Скидки", true, false, Set.of(UserRole.ADMIN)),
    USERS("Пользователи", true, false, Set.of(UserRole.ADMIN)),
    QUIT_ADMIN_PANEL("Выйти", false, false, Set.of(UserRole.ADMIN)),
    TURNING_DELIVERY_TYPE("Вкл/выкл способов доставки", false, false, Set.of(UserRole.ADMIN)),
    BACKUP_DB("/backupdb", false, false, Set.of(UserRole.ADMIN)),

    DEALS_COUNT("Кол-во возможных сделок", false, false, Set.of(UserRole.ADMIN)),

    /**
     * DISCOUNTS
     */
    RANK_DISCOUNT("Ранговая скидка(персональная)", false, false, Set.of(UserRole.ADMIN)),
    CHANGE_RANK_DISCOUNT("change_rank_discount", false, false, Set.of(UserRole.ADMIN)),
    PERSONAL_BUY_DISCOUNT("Персональная, покупка", false, false, Set.of(UserRole.ADMIN)),
    PERSONAL_SELL_DISCOUNT("Персональная, продажа", false, false, Set.of(UserRole.ADMIN)),
    BULK_DISCOUNTS("Оптовые скидки", false, false, Set.of(UserRole.ADMIN)),
    REFERRAL_PERCENT("Процент реферала", false, false, Set.of(UserRole.ADMIN)),
    TURN_RANK_DISCOUNT("Ранговая скидка(для всех)", false, false, Set.of(UserRole.ADMIN)),
    TURNING_RANK_DISCOUNT("turning_rd", false, false, Set.of(UserRole.ADMIN)),

    /**
     * TURNING CURRENCIES
     */
    TURN_ON_CURRENCY("turn_on_currency", false, false, Set.of(UserRole.ADMIN)),
    TURN_OFF_CURRENCY("turn_off_currency", false, false, Set.of(UserRole.ADMIN)),

    /**
     * EDIT CONTACTS
     */
    ADD_CONTACT("Добавить контакт", false, false, Set.of(UserRole.ADMIN)),
    DELETE_CONTACT("Удалить контакт", false, false, Set.of(UserRole.ADMIN)),

    /**
     * SEND MESSAGES
     */
    MAILING_LIST("Рассылка", false, false, Set.of(UserRole.ADMIN)),
    SEND_MESSAGE_TO_USER("Сообщение пользователю", false, false, Set.of(UserRole.ADMIN)),

    /**
     * BOT SETTINGS
     */
    CURRENT_DATA("Текущие данные", false, false, Set.of(UserRole.ADMIN)),
    ON_BOT("Вкл.бота", false, false, Set.of(UserRole.ADMIN)),
    OFF_BOT("Выкл.бота", false, false, Set.of(UserRole.ADMIN)),
    BOT_MESSAGES("Сообщения бота", false, false, Set.of(UserRole.ADMIN)),
    BOT_VARIABLES("Переменные бота", false, false, Set.of(UserRole.ADMIN)),
    SYSTEM_MESSAGES("Сис.сообщения", false, false, Set.of(UserRole.ADMIN)),
    PAYMENT_TYPES("Типы оплаты", false, false, Set.of(UserRole.ADMIN)),

    /**
     * DEAL
     */
    DEAL("deal_proc", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    PAID(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("PAID"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    CANCEL_DEAL("Отменить заявку", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    DELETE_DEAL("Удалить заявку", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    SHOW_DEAL("Показать", false, false, Set.of(UserRole.ADMIN)),
    SHOW_API_DEAL("show_api_deal", false, false, Set.of(UserRole.ADMIN)),
    DELETE_USER_DEAL("delete_deal", false, false, Set.of(UserRole.ADMIN)),
    DELETE_DEAL_AND_BLOCK_USER("deleteDeal_and_block_user", false, false, Set.of(UserRole.ADMIN)),
    CONFIRM_USER_DEAL("confirm_deal", false, false, Set.of(UserRole.ADMIN)),
    ADDITIONAL_VERIFICATION("add_verification", false, false, Set.of(UserRole.ADMIN)),
    USER_ADDITIONAL_VERIFICATION("user_verification", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    SHARE_REVIEW("share_review", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    CHOOSING_FIAT_CURRENCY("chs_fc", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    USE_PROMO(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("USE_PROMO"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    DONT_USE_PROMO(PropertiesPath.BUTTONS_DESIGN_PROPERTIES.getString("DONT_USE_PROMO"), false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),

    /**
     * REQUESTS
     */
    NEW_DEALS("Новые заявки", false, false, Set.of(UserRole.ADMIN)),
    NEW_WITHDRAWALS("Вывод средств", false, false, Set.of(UserRole.ADMIN)),
    NEW_REVIEWS("Новые отзывы", false, false, Set.of(UserRole.ADMIN)),

    PUBLISH_REVIEW("pub_review", false, false, Set.of(UserRole.ADMIN)),
    DELETE_REVIEW("del_review", false, false, Set.of(UserRole.ADMIN)),

    /**
     * REPORTS
     */
    USERS_REPORT("Отчет по пользователям", false, false, Set.of(UserRole.ADMIN)),
    USER_INFORMATION("Информация о пользователе", false, false, Set.of(UserRole.ADMIN)),
    USERS_DEALS_REPORT("Отчет сделки пользователей", false, false, Set.of(UserRole.ADMIN)),
    DEAL_REPORTS("Отчет по сделкам", false, false, Set.of(UserRole.ADMIN)),
    PARTNERS_REPORT("Отчет по партнерам", false, false, Set.of(UserRole.ADMIN)),
    CHECKS_FOR_DATE("Чеки по дате", false, false, Set.of(UserRole.ADMIN)),
    SEND_CHECKS_FOR_DATE("send_checks_for_dats", false, false, Set.of(UserRole.ADMIN)),
    LOTTERY_REPORT("Отчет по лотереи", false, false, Set.of(UserRole.ADMIN)),

    CHANNEL_POST("channel_post", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),

    /**
     * RECEIPTS
     */
    CONTINUE("Продолжить", false, false, Set.of(UserRole.ADMIN)),
    RECEIPTS_CANCEL_DEAL("Отменить сделку", false, false, Set.of(UserRole.ADMIN)),

    /**
     * PAYMENT TYPES
     */
    NEW_PAYMENT_TYPE("Создать тип оплаты", false, false, Set.of(UserRole.ADMIN)),
    DELETE_PAYMENT_TYPE("Удалить тип оплаты", false, false, Set.of(UserRole.ADMIN)),
    DELETING_PAYMENT_TYPE("deleting_pt", false, false, Set.of(UserRole.ADMIN)),
    NEW_PAYMENT_TYPE_REQUISITE("Создать реквизит", false, false, Set.of(UserRole.ADMIN)),
    DELETE_PAYMENT_TYPE_REQUISITE("Удалить реквизит", false, false, Set.of(UserRole.ADMIN)),
    DELETING_PAYMENT_TYPE_REQUISITE("delete_ptr", false, false, Set.of(UserRole.ADMIN)),
    TURN_PAYMENT_TYPES("Включение типов оплат", false, false, Set.of(UserRole.ADMIN)),
    TURNING_PAYMENT_TYPES("turning_pt", false, false, Set.of(UserRole.ADMIN)),
    CHANGE_MIN_SUM("Мин.сумма", false, false, Set.of(UserRole.ADMIN)),
    TURN_DYNAMIC_REQUISITES("Динамические реквизиты", false, false, Set.of(UserRole.ADMIN)),
    TURNING_DYNAMIC_REQUISITES("turning_dr", false, false, Set.of(UserRole.ADMIN)),

    /**
     * ANTISPAM
     */
    SHOW_SPAM_BANNED_USER("show_sb_user", false, false, Set.of(UserRole.ADMIN)),
    KEEP_SPAM_BAN("keep_sb", false, false, Set.of(UserRole.ADMIN)),
    SPAM_UNBAN("spam_unban", false, false, Set.of(UserRole.ADMIN)),
    NEW_SPAM_BANS("Антиспам блоки", false, false, Set.of(UserRole.ADMIN)),

    /**
     * USERS STATES
     */
    NONE_CALCULATOR("none_calc", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    INLINE_QUERY_CALCULATOR("inline_q_calc", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    INLINE_CALCULATOR("inline_calculator", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),

    WEB_ADMIN_PANEL("Веб админ-панель", false, false, Set.of(UserRole.ADMIN)),

    /**
     * API DEALS
     */
    CONFIRM_API_DEAL("confirm_api_deal", false, false, Set.of(UserRole.ADMIN)),
    CANCEL_API_DEAL("cancel_api_deal", false, false, Set.of(UserRole.ADMIN)),
    NEW_API_DEALS("Новые API заявки", false, false, Set.of(UserRole.ADMIN)),

    /**
     * TURNING DELIVERY
     */
    TURN_PROCESS_DELIVERY("turn_process_delivery", false, false, Set.of(UserRole.ADMIN)),

    /**
     * WEB
     */
    SUBMIT_LOGIN("Подтвердить вход", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    SUBMIT_REGISTER("Подтвердить регистрацию", false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN)),
    LOGOUT("Закрыть сессию",  false, false, Set.of(UserRole.USER, UserRole.OPERATOR, UserRole.ADMIN))
    ;

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
                return findByText(update.getMessage().getText());
            case CALLBACK_QUERY:
                return findByText(update.getCallbackQuery().getData());
            case INLINE_QUERY:
                return findByText(update.getInlineQuery().getQuery());
            case CHANNEL_POST:
                return Command.CHANNEL_POST;
            default:
                return Command.START;
        }
    }

    public static Command findByText(String value) {
        return Arrays.stream(Command.values())
                .filter(command -> (Objects.nonNull(command.getText()) && value.startsWith(command.getText())) || value.startsWith(command.name()))
                .findFirst()
                .orElse(null);
    }
}
