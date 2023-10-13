package tgb.btc.rce.enums;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

@Slf4j
public enum Command {
    /*
     * CallbackQuery
     */

    START("/start", false, false, false),
    NONE("none", false, true, false),

    /*
      Reply
     */

    /** UTIL */
    BACK(BotProperties.BUTTONS_DESIGN.getString("BACK"), false, true, false),
    ADMIN_BACK("Назад", true, false, false),
    CANCEL("Отмена", false, false, false),
    SHARE_CONTACT("Поделиться контактом", false, false, false),
    BOT_OFFED("bot_offed", false, true, false),
    INLINE_DELETE("inline_delete", false, true, false),
    CAPTCHA("captcha", false, false, false),

    /**
     * HIDDEN
     */
    DELETE_USER("/deleteuser", true, false, true),
    CREATE_USER_DATA("/createuserdata", true, false, true),
    MAKE_ADMIN("/makeadmin", true, false, true),
    HELP("/help", true, false, true),

    /** MAIN */
    BUY_BITCOIN(BotProperties.BUTTONS_DESIGN.getString("BUY_BITCOIN"), false, false, false),
    SELL_BITCOIN(BotProperties.BUTTONS_DESIGN.getString("SELL_BITCOIN"), false, false, false),
    CONTACTS(BotProperties.BUTTONS_DESIGN.getString("CONTACTS"), false, true, false),
    DRAWS(BotProperties.BUTTONS_DESIGN.getString("DRAWS"), false, true, false),
    REFERRAL(BotProperties.BUTTONS_DESIGN.getString("REFERRAL"), false, false, false),
    ADMIN_PANEL("Админ панель", true, true, false),

    /** DRAWS */
    LOTTERY(BotProperties.BUTTONS_DESIGN.getString("LOTTERY"), false, false, false),
    ROULETTE(BotProperties.BUTTONS_DESIGN.getString("ROULETTE"), false, true, false),

    /** REFERRAL */
    WITHDRAWAL_OF_FUNDS("withdrawal", false, false, false),
    SHOW_WITHDRAWAL_REQUEST("show_withdrawal", false, false, false),
    SEND_LINK(BotProperties.BOT_CONFIG.getString("bot.link"), false, false, false),
    HIDE_WITHDRAWAL("hide_withdrawal", true, false, false),
    CHANGE_REFERRAL_BALANCE("change_ref", true, false, false),
    DELETE_WITHDRAWAL_REQUEST("withdrawal_delete", false, false, false),

    /** ADMIN PANEL */
    REQUESTS("Заявки", true, true, false),
    SEND_MESSAGES("Отправка сообщений", true, true, false),
    BAN_UNBAN("Бан/разбан", true, false, false),
    BOT_SETTINGS("Настройки бота", true, true, false),
    REPORTS("Отчеты", true, true, false),
    EDIT_CONTACTS("Редактирование контактов", true, true, false),
    USER_REFERRAL_BALANCE("Реф.баланс юзера", true, false, false),
    CHANGE_USD_COURSE("Курс доллара", true, false, false),
    TURNING_CURRENCY("Включение криптовалют", true, false, false),
    DISCOUNTS("Скидки", true, true, false),
    USERS("Пользователи", true, true, false),
    QUIT_ADMIN_PANEL("Выйти", true, false, false),

    /** DISCOUNTS */
    RANK_DISCOUNT("Ранговая скидка(персональная)", true, false, false),
    CHANGE_RANK_DISCOUNT("change_rank_discount", true, false, false),
    PERSONAL_BUY_DISCOUNT("Персональная, покупка", true, false, false),
    PERSONAL_SELL_DISCOUNT("Персональная, продажа", true, false, false),
    BULK_DISCOUNTS("Оптовые скидки", true, false, false),
    REFERRAL_PERCENT("Процент реферала", true, false, false),
    TURN_RANK_DISCOUNT("Ранговая скидка(для всех)", true, false, false),
    TURNING_RANK_DISCOUNT("turning_rd", true, false, false),

    /** TURNING CURRENCIES */
    TURN_ON_CURRENCY("turn_on_currency", true, false, false),
    TURN_OFF_CURRENCY("turn_off_currency", true, false, false),

    /** EDIT CONTACTS */
    ADD_CONTACT("Добавить контакт", true, false, false),
    DELETE_CONTACT("Удалить контакт", true, false, false),

    /** SEND MESSAGES */
    MAILING_LIST("Рассылка", true, false, false),
    SEND_MESSAGE_TO_USER("Сообщение пользователю", true, false, false),

    /** BOT SETTINGS */
    CURRENT_DATA("Текущие данные", true, false, false),
    ON_BOT("Вкл.бота", true, false, false),
    OFF_BOT("Выкл.бота", true, false, false),
    BOT_MESSAGES("Сообщения бота", true, false, false),
    BOT_VARIABLES("Переменные бота", true, false, false),
    SYSTEM_MESSAGES("Сис.сообщения", true, false, false),
    PAYMENT_TYPES("Типы оплаты", true, false, false),

    /** DEAL */
    DEAL("deal_proc", false, false, false),
    PAID(BotProperties.BUTTONS_DESIGN.getString("PAID"), false, false, false),
    CANCEL_DEAL("Отменить заявку", false, false, false),
    DELETE_DEAL("Удалить заявку", false, false, false),
    SHOW_DEAL("Показать", true, false, false),
    SHOW_API_DEAL("show_api_deal", true, false, false),
    DELETE_USER_DEAL("delete_deal", true, false, false),
    DELETE_DEAL_AND_BLOCK_USER("deleteDeal_and_block_user", true, false, false),
    CONFIRM_USER_DEAL("confirm_deal", true, false, false),
    ADDITIONAL_VERIFICATION("add_verification", true, false, false),
    USER_ADDITIONAL_VERIFICATION("user_verification", false, false, false),
    SHARE_REVIEW("share_review", false, false, false),
    CHOOSING_FIAT_CURRENCY("chs_fc", false, false, false),
    USE_PROMO(BotProperties.BUTTONS_DESIGN.getString("USE_PROMO"), false, false, false),
    DONT_USE_PROMO(BotProperties.BUTTONS_DESIGN.getString("DONT_USE_PROMO"), false, false, false),

    /** REQUESTS */
    NEW_DEALS("Новые заявки", true, false, false),
    NEW_WITHDRAWALS("Вывод средств", true, false, false),
    NEW_REVIEWS("Новые отзывы", true, false, false),

    PUBLISH_REVIEW("pub_review", true, false, false),
    DELETE_REVIEW("del_review", true, false, false),

    /** REPORTS */
    USERS_REPORT("Отчет по пользователям", true, false, false),
    USER_INFORMATION("Информация о пользователе", true, false, false),
    USERS_DEALS_REPORT("Отчет сделки пользователей", true, false, false),
    DEAL_REPORTS("Отчет по сделкам", true, false, false),
    PARTNERS_REPORT("Отчет по партнерам", true, false, false),
    CHECKS_FOR_DATE("Чеки по дате", true, false, false),
    SEND_CHECKS_FOR_DATE("send_checks_for_dats", true, false, false),
    LOTTERY_REPORT("Отчет по лотереи", true, false, false),

    CHANNEL_POST("channel_post", false, false, false),

    /** RECEIPTS */
    CONTINUE("Продолжить", true, false, false),
    RECEIPTS_CANCEL_DEAL("Отменить сделку", true, false, false),

    /**
     * PAYMENT TYPES
     */
    NEW_PAYMENT_TYPE("Создать тип оплаты", true, false, false),
    DELETE_PAYMENT_TYPE("Удалить тип оплаты", true, false, false),
    DELETING_PAYMENT_TYPE("deleting_pt", true, false, false),
    NEW_PAYMENT_TYPE_REQUISITE("Создать реквизит", true, false, false),
    DELETE_PAYMENT_TYPE_REQUISITE("Удалить реквизит", true, false, false),
    DELETING_PAYMENT_TYPE_REQUISITE("delete_ptr", true, false, false),
    TURN_PAYMENT_TYPES("Включение типов оплат", true, false, false),
    TURNING_PAYMENT_TYPES("turning_pt", true, false, false),
    CHANGE_MIN_SUM("Мин.сумма", true, false, false),
    TURN_DYNAMIC_REQUISITES("Динамические реквизиты", true, false, false),
    TURNING_DYNAMIC_REQUISITES("turning_dr", true, false, false),

    /**
     * ANTISPAM
     */
    SHOW_SPAM_BANNED_USER("show_sb_user", true, false, false),
    KEEP_SPAM_BAN("keep_sb", true, false, false),
    SPAM_UNBAN("spam_unban", true, false, false),
    NEW_SPAM_BANS("Антиспам блоки", true, false, false),

    /**
     * USERS STATES
     */
    NONE_CALCULATOR("none_calc", false, false, false),
    INLINE_QUERY_CALCULATOR("inline_q_calc", false, false, false),
    INLINE_CALCULATOR("inline_calculator", false, false, false),

    WEB_ADMIN_PANEL("Веб админ-панель", true, false, false),

    /**
     * API DEALS
     */
    CONFIRM_API_DEAL("confirm_api_deal", true, false, false),
    CANCEL_API_DEAL("cancel_api_deal", true, false, false),
    NEW_API_DEALS("Новые API заявки", true, false, false)
    ;

    final String text;
    final boolean isAdmin;
    final boolean isSimple;
    final boolean isHidden;

    Command(String text, boolean isAdmin, boolean isSimple, boolean isHidden) {
        this.text = text;
        this.isAdmin = isAdmin;
        this.isSimple = isSimple;
        this.isHidden = isHidden;
    }

    public boolean isSimple() {
        return isSimple;
    }

    public String getText() {
        return text;
    }

    public boolean isAdmin() {
        return isAdmin;
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
                .filter(command -> value.startsWith(command.getText()))
                .findFirst()
                .orElse(null);
    }
}
