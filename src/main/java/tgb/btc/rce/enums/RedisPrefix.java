package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedisPrefix {
    MESSAGE_ID("message_id_"),
    DEAL_PID("deal_pid_");

    final String prefix;
}
