package tgb.btc.lib.enums;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

@Slf4j
public enum Command {
    /*
     * CallbackQuery
     */

    START("/start", false, false),
    NONE("none", false, true),

    /*
      Reply
     */

    /** UTIL */
    BACK("◀️ Назад", false, true),
    ADMIN_BACK("Назад", true, false),
    CANCEL("Отмена", false, false),
    SHARE_CONTACT("Поделиться контактом", false, false),
    BOT_OFFED("bot_offed", false, true),
    INLINE_DELETE("inline_delete", false, true),
    CAPTCHA("captcha", false, false),

    /**
     * HIDDEN
     */
    DELETE_USER("/deleteuser", true, false),
    CREATE_USER_DATA("/createuserdata", true, false),
    MAKE_ADMIN("/makeadmin", true, false),

    /** MAIN */
    BUY_BITCOIN("\uD83D\uDCB0 Купить", false, false),
    SELL_BITCOIN("\uD83D\uDCC8 Продать", false, false),
    CONTACTS("\uD83D\uDEC3 Контакты", false, true),
    DRAWS("\uD83C\uDFB0 Розыгрыши", false, true),
    REFERRAL("\uD83E\uDD1D Реферальная программа", false, false),
    ADMIN_PANEL("Админ панель", true, true),

    /** DRAWS */
    LOTTERY("\uD83C\uDFB0 Лотерея", false, false),
    ROULETTE("\uD83C\uDFB0 Рулетка", false, true),

    /** REFERRAL */
    WITHDRAWAL_OF_FUNDS("withdrawal", false, false),
    SHOW_WITHDRAWAL_REQUEST("show_withdrawal", false, false),
    SEND_LINK(BotProperties.BOT_CONFIG_PROPERTIES.getString("bot.link"), false, false),
    HIDE_WITHDRAWAL("hide_withdrawal", true, false),
    CHANGE_REFERRAL_BALANCE("change_ref", true, false),
    DELETE_WITHDRAWAL_REQUEST("withdrawal_delete", false, false),

    /** ADMIN PANEL */
    REQUESTS("Заявки", true, true),
    SEND_MESSAGES("Отправка сообщений", true, true),
    BAN_UNBAN("Бан/разбан", true, false),
    BOT_SETTINGS("Настройки бота", true, true),
    REPORTS("Отчеты", true, true),
    EDIT_CONTACTS("Редактирование контактов", true, true),
    USER_REFERRAL_BALANCE("Реф.баланс юзера", true, false),
    CHANGE_USD_COURSE("Курс доллара", true, false),
    TURNING_CURRENCY("Включение криптовалют", true, false),
    DISCOUNTS("Скидки", true, true),
    USERS("Пользователи", true, true),
    QUIT_ADMIN_PANEL("Выйти", true, false),

    /** DISCOUNTS */
    RANK_DISCOUNT("Ранговая скидка(персональная)", true, false),
    CHANGE_RANK_DISCOUNT("change_rank_discount", true, false),
    PERSONAL_BUY_DISCOUNT("Персональная, покупка", true, false),
    PERSONAL_SELL_DISCOUNT("Персональная, продажа", true, false),
    BULK_DISCOUNTS("Оптовые скидки", true, false),
    REFERRAL_PERCENT("Процент реферала", true, false),
    TURN_RANK_DISCOUNT("Ранговая скидка(для всех)", true, false),
    TURNING_RANK_DISCOUNT("turning_rd", true, false),

    /** TURNING CURRENCIES */
    TURN_ON_CURRENCY("turn_on_currency", true, false),
    TURN_OFF_CURRENCY("turn_off_currency", true, false),

    /** EDIT CONTACTS */
    ADD_CONTACT("Добавить контакт", true, false),
    DELETE_CONTACT("Удалить контакт", true, false),

    /** SEND MESSAGES */
    MAILING_LIST("Рассылка", true, false),
    SEND_MESSAGE_TO_USER("Сообщение пользователю", true, false),

    /** BOT SETTINGS */
    CURRENT_DATA("Текущие данные", true, false),
    ON_BOT("Вкл.бота", true, false),
    OFF_BOT("Выкл.бота", true, false),
    BOT_MESSAGES("Сообщения бота", true, false),
    BOT_VARIABLES("Переменные бота", true, false),
    SYSTEM_MESSAGES("Сис.сообщения", true, false),
    PAYMENT_TYPES("Типы оплаты", true, false),

    /** DEAL */
    DEAL("deal_proc", false, false),
    PAID("Я оплатил(а)", false, false),
    CANCEL_DEAL("Отменить заявку", false, false),
    DELETE_DEAL("Удалить заявку", false, false),
    SHOW_DEAL("Показать", true, false),
    DELETE_USER_DEAL("delete_deal", true, false),
    DELETE_DEAL_AND_BLOCK_USER("deleteDeal_and_block_user", true, false),
    CONFIRM_USER_DEAL("confirm_deal", true, false),
    ADDITIONAL_VERIFICATION("add_verification", true, false),
    USER_ADDITIONAL_VERIFICATION("user_verification", false, false),
    SHARE_REVIEW("share_review", false, false),
    CHOOSING_FIAT_CURRENCY("chs_fc", false, false),

    /** REQUESTS */
    NEW_DEALS("Новые заявки", true, false),
    NEW_WITHDRAWALS("Вывод средств", true, false),
    NEW_REVIEWS("Новые отзывы", true, false),

    PUBLISH_REVIEW("pub_review", true, false),
    DELETE_REVIEW("del_review", true, false),

    /** REPORTS */
    USERS_REPORT("Отчет по пользователям", true, false),
    USER_INFORMATION("Информация о пользователе", true, false),
    USERS_DEALS_REPORT("Отчет сделки пользователей", true, false),
    DEAL_REPORTS("Отчет по сделкам", true, false),
    PARTNERS_REPORT("Отчет по партнерам", true, false),
    CHECKS_FOR_DATE("Чеки по дате", true, false),
    SEND_CHECKS_FOR_DATE("send_checks_for_dats", true, false),
    LOTTERY_REPORT("Отчет по лотереи", true, false),

    CHANNEL_POST("channel_post", false, false),

    /** RECEIPTS */
    CONTINUE("Продолжить", true, false),
    RECEIPTS_CANCEL_DEAL("Отменить сделку", true, false),

    /**
     * PAYMENT TYPES
     */
    NEW_PAYMENT_TYPE("Создать тип оплаты", true, false),
    DELETE_PAYMENT_TYPE("Удалить тип оплаты", true, false),
    DELETING_PAYMENT_TYPE("deleting_pt", true, false),
    NEW_PAYMENT_TYPE_REQUISITE("Создать реквизит", true, false),
    DELETE_PAYMENT_TYPE_REQUISITE("Удалить реквизит", true, false),
    DELETING_PAYMENT_TYPE_REQUISITE("delete_ptr", true, false),
    TURN_PAYMENT_TYPES("Включение типов оплат", true, false),
    TURNING_PAYMENT_TYPES("turning_pt", true, false),
    CHANGE_MIN_SUM("Мин.сумма", true, false),
    TURN_DYNAMIC_REQUISITES("Динамические реквизиты", true, false),
    TURNING_DYNAMIC_REQUISITES("turning_dr", true, false),

    /**
     * ANTISPAM
     */
    SHOW_SPAM_BANNED_USER("show_sb_user", true, false),
    KEEP_SPAM_BAN("keep_sb", true, false),
    SPAM_UNBAN("spam_unban", true, false),
    NEW_SPAM_BANS("Антиспам блоки", true, false),

    /**
     * USERS STATES
     */
    NONE_CALCULATOR("none_calc", false, false),
    INLINE_QUERY_CALCULATOR("inline_q_calc", false, false),
    INLINE_CALCULATOR("inline_calculator", false, false)
    ;

    final String text;
    final boolean isAdmin;
    final boolean isSimple;

    Command(String text, boolean isAdmin, boolean isSimple) {
        this.text = text;
        this.isAdmin = isAdmin;
        this.isSimple = isSimple;
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
