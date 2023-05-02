package tgb.btc.rce.enums;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.util.BotPropertiesUtil;

import java.util.Arrays;

@Slf4j
public enum Command {
    /*
     * CallbackQuery
     */

    START("/start", false),
    NONE("none", false),

    /*
      Reply
     */

    /** UTIL */
    BACK("◀️ Назад", false),
    ADMIN_BACK("Назад", true),
    CANCEL("Отмена", false),
    SHARE_CONTACT("Поделиться контактом", false),
    BOT_OFFED("bot_offed", false),
    INLINE_DELETE("inline_delete", false),

    /** MAIN */
    BUY_BITCOIN("\uD83D\uDCB0 Купить", false),
    SELL_BITCOIN("\uD83D\uDCC8 Продать", false),
    CONTACTS("\uD83D\uDEC3 Контакты", false),
    DRAWS("\uD83C\uDFB0 Розыгрыши", false),
    REFERRAL("\uD83E\uDD1D Реферальная программа", false),
    ADMIN_PANEL("Админ панель", true),

    /** DRAWS */
    LOTTERY("\uD83C\uDFB0 Лотерея", false),
    ROULETTE("\uD83C\uDFB0 Рулетка", false),

    /** REFERRAL */
    WITHDRAWAL_OF_FUNDS("withdrawal", false),
    SHOW_WITHDRAWAL_REQUEST("show_withdrawal", false),
    SEND_LINK(BotPropertiesUtil.getProperty("bot.link"), false),
    HIDE_WITHDRAWAL("hide_withdrawal", true),
    CHANGE_REFERRAL_BALANCE("change_ref", true),
    DELETE_WITHDRAWAL_REQUEST("withdrawal_delete", false),

    /** ADMIN PANEL */
    REQUESTS("Заявки", true),
    SEND_MESSAGES("Отправка сообщений", true),
    BAN_UNBAN("Бан/разбан", true),
    BOT_SETTINGS("Настройки бота", true),
    REPORTS("Отчеты", true),
    EDIT_CONTACTS("Редактирование контактов", true),
    USER_REFERRAL_BALANCE("Реф.баланс юзера", true),
    CHANGE_USD_COURSE("Курс доллара", true),
    TURNING_CURRENCY("Включение криптовалют", true),
    DISCOUNTS("Скидки", true),
    QUIT_ADMIN_PANEL("Выйти", true),

    /** DISCOUNTS */
    RANK_DISCOUNT("Ранговая скидка(персональная)", true),
    CHANGE_RANK_DISCOUNT("change_rank_discount", true),
    PERSONAL_BUY_DISCOUNT("Персональная, покупка", true),
    PERSONAL_SELL_DISCOUNT("Персональная, продажа", true),
    BULK_DISCOUNTS("Оптовые скидки", true),
    REFERRAL_PERCENT("Процент реферала", true),
    TURN_RANK_DISCOUNT("Ранговая скидка(для всех)", true),
    TURNING_RANK_DISCOUNT("turning_rd", true),

    /** TURNING CURRENCIES */
    TURN_ON_CURRENCY("turn_on_currency", true),
    TURN_OFF_CURRENCY("turn_off_currency", true),

    /** EDIT CONTACTS */
    ADD_CONTACT("Добавить контакт", true),
    DELETE_CONTACT("Удалить контакт", true),

    /** SEND MESSAGES */
    MAILING_LIST("Рассылка", true),
    SEND_MESSAGE_TO_USER("Сообщение пользователю", true),

    /** BOT SETTINGS */
    CURRENT_DATA("Текущие данные", true),
    ON_BOT("Вкл.бота", true),
    OFF_BOT("Выкл.бота", true),
    BOT_MESSAGES("Сообщения бота", true),
    BOT_VARIABLES("Переменные бота", true),
    SYSTEM_MESSAGES("Сис.сообщения", true),
    PAYMENT_TYPES_OLD("Типы оплаты", true),
    PAYMENT_TYPES("New Типы оплаты", true),
    PAYMENT_REQUISITES("Реквизиты оплат", true),

    /** DEAL */
    PAID("Я оплатил(а)", false),
    CANCEL_DEAL("Отменить заявку", false),
    DELETE_DEAL("Удалить заявку", false),
    SHOW_DEAL("Показать", true),
    DELETE_USER_DEAL("delete_deal", true),
    DELETE_DEAL_AND_BLOCK_USER("deleteDeal_and_block_user", true),
    CONFIRM_USER_DEAL("confirm_deal", true),
    ADDITIONAL_VERIFICATION("add_verification", true),
    USER_ADDITIONAL_VERIFICATION("user_verification", false),
    SHARE_REVIEW("share_review", false),

    /** REQUESTS */
    NEW_DEALS("Новые заявки", true),
    NEW_WITHDRAWALS("Вывод средств", true),
    NEW_REVIEWS("Новые отзывы", true),

    PUBLISH_REVIEW("pub_review", true),
    DELETE_REVIEW("del_review", true),

    /** REPORTS */
    USERS_REPORT("Отчет по пользователям", true),
    USERS_DEALS_REPORT("Отчет сделки пользователей", true),
    DEAL_REPORTS("Отчет по сделкам", true),
    PARTNERS_REPORT("Отчет по партнерам", true),
    CHECKS_FOR_DATE("Чеки по дате", true),
    SEND_CHECKS_FOR_DATE("send_checks_for_dats", true),

    CHANNEL_POST("channel_post", false),

    /** RECEIPTS */
    CONTINUE("Продолжить", true),
    RECEIPTS_CANCEL_DEAL("Отменить сделку", true),

    /**
     * PAYMENT TYPES
     */
    NEW_PAYMENT_TYPE("Создать тип оплаты", true),
    DELETE_PAYMENT_TYPE("Удалить тип оплаты", true),
    DELETING_PAYMENT_TYPE("deleting_pt", true),
    NEW_PAYMENT_TYPE_REQUISITE("Создать реквизит", true),
    DELETE_PAYMENT_TYPE_REQUISITE("Удалить реквизит", true),
    DELETING_PAYMENT_TYPE_REQUISITE("delete_ptr", true),
    TURN_PAYMENT_TYPES("Включение типов оплат", true),
    TURNING_PAYMENT_TYPES("turning_pt", true),
    CHANGE_MIN_SUM("Мин.сумма", true),
    TURN_DYNAMIC_REQUISITES("Динамические реквизиты", true),
    TURNING_DYNAMIC_REQUISITES("turning_dr", true),

    CREATE_USER_DATA("/createuserdata", true)
    ;

    final String text;
    final boolean isAdmin;

    Command(String text, boolean isAdmin) {
        this.text = text;
        this.isAdmin = isAdmin;
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
