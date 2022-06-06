package tgb.btc.rce.enums;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.exception.BaseException;

import java.util.Arrays;

@Slf4j
public enum Command {
    /*
     * CallbackQuery
     */

    START("/start", false),

    /*
      Reply
     */

    /** UTIL */
    BACK("◀️ Назад", false),
    ADMIN_BACK("Назад", true),
    CANCEL("Отмена", false),
    SHARE_CONTACT("Поделиться контактом", false),

    /** MAIN */
    BUY_BITCOIN("\uD83D\uDCB0 Купить", false),
    SELL_BITCOIN("\uD83D\uDCC8 Продать", false),
    CONTACTS("\uD83D\uDEC3 Контакты", false),
    DRAWS("\uD83C\uDFB0 Розыгрыши", false),
    REFERRAL("\uD83E\uDD1D Реферальная программа", false),
    ADMIN_PANEL("Админ панель", true),

    /** DRAWS */
    LOTTERY("\uD83C\uDFB0 Лотерея", false),
    ROULETTE("\uD83C\uDFB0 Рулетка", false), // TODO поменять смайл

    /** REFERRAL */
    WITHDRAWAL_OF_FUNDS("withdrawal", false),
    SHOW_WITHDRAWAL_REQUEST("show_withdrawal", false),
    SEND_LINK("send_link", false),
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
    QUIT_ADMIN_PANEL("Выйти", true),
    USER_REFERRAL_BALANCE("Реф.баланс юзера", true),

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
    PAYMENT_TYPES("Типы оплаты", true),

    /** DEAL */
    PAID("Я оплатил(а)", false),
    CANCEL_DEAL("Отменить заявку", false),
    DELETE_DEAL("Удалить заявку", false),
    SHOW_DEAL("Показать", true),
    DELETE_USER_DEAL("delete_deal", true),
    CONFIRM_USER_DEAL("confirm_deal", true),
    ADDITIONAL_VERIFICATION("add_verification", true),
    USER_ADDITIONAL_VERIFICATION("user_verification", true),

    /** REQUESTS */
    NEW_DEALS("Новые заявки", true),
    NEW_WITHDRAWALS("Вывод средств", true),

    /** REPORTS */
    USERS_REPORT("Отчет по пользователям", true),
    DEAL_REPORTS("Отчет по сделкам", true),
    PARTNERS_REPORT("Отчет по партнерам", true);

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
            default:
                throw new BaseException("Тип апдейта не найден: " + update);
        }
    }

    public static Command findByText(String value) {
        return Arrays.stream(Command.values())
                .filter(command -> value.startsWith(command.getText()))
                .findFirst()
                .orElseThrow(() -> new BaseException("Команда для \"" + value + "\" не найдена."));
    }
}
