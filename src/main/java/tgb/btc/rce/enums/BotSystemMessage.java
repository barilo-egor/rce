package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BotSystemMessage {
    MESSAGE_SENT("\n\n <b>---СООБЩЕНИЕ ОТПРАВЛЕНО---</b>");

    final String text;
}
