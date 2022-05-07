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

    /** MAIN */
    BUY_BITCOIN("\uD83D\uDCB0 Купить"),
    SELL_BITCOIN("\uD83D\uDCC8 Продать"),
    CONTACTS("\uD83D\uDEC3 Контакты"),
    LOTTERY("\uD83C\uDFB0 Лотерея"),
    RULES("\uD83D\uDDD2 Правила"),
    ADMIN_PANEL("Админ панель");

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
                .filter(command -> command.getText().equals(value))
                .findFirst()
                .orElseThrow(() -> new BaseException("Команда для \"" + value + "\" не найдена."));
    }
}
