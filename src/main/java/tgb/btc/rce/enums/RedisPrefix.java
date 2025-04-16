package tgb.btc.rce.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedisPrefix {
    MESSAGE_ID("message_id_"),
    DEAL_PID("deal_pid_"),
    DEAL_TYPE("deal_type_"),
    FIAT_CURRENCY("fiat_currency_"),
    PAYMENT_TYPE_PID("payment_type_pid_"),
    DEALS_SIZE("deals_size_"),
    TOTAL_AMOUNT("total_amount_"),
    STEP("step_"),;

    final String prefix;
}
