package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BotSystemMessage {
    MESSAGE_SENT("\n\n <b>---СООБЩЕНИЕ ОТПРАВЛЕНО---</b>"),
    ENTER_VALID_INPUT("Введите валидное значение."),
    INPUT_WALLET("Введите %s-адрес кошелька, куда вы хотите отправить %s%s.%s");

    final String text;
}
