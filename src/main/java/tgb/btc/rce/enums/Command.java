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

    START("/start"),

    /*
      Reply
     */

    /** UTIL */
    BACK("◀️ Назад"),
    CANCEL("Отмена"),

    /** MAIN */
    BUY_BITCOIN("\uD83D\uDCB0 Купить"),
    SELL_BITCOIN("\uD83D\uDCC8 Продать"),
    CONTACTS("\uD83D\uDEC3 Контакты"),
    DRAWS("\uD83C\uDFB0 Розыгрыши"),
    REFERRAL("\uD83E\uDD1D Реферальная программа"),
    ADMIN_PANEL("Админ панель"),

    /** DRAWS */
    LOTTERY("\uD83C\uDFB0 Лотерея"),
    ROULETTE("\uD83C\uDFB0 Рулетка"), // TODO поменять смайл

    /** REFERRAL */
    WITHDRAWAL_OF_FUNDS("withdrawal"),
    SHOW_WITHDRAWAL_REQUEST("show_withdrawal");

    final String text;

    Command(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
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
