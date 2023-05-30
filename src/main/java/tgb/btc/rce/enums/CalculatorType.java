package tgb.btc.rce.enums;

import org.springframework.context.annotation.Condition;

public enum CalculatorType {
    NONE,
    INLINE_QUERY;

    public static final CalculatorType CURRENT =
            CalculatorType.valueOf(BotProperties.MODULES_PROPERTIES.getString("calculator.type"));

    public boolean isCurrent() {
        return this.equals(CURRENT);
    }
}
