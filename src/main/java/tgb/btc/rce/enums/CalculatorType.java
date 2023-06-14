package tgb.btc.rce.enums;

import tgb.btc.rce.service.Module;

public enum CalculatorType implements Module {
    NONE,
    INLINE_QUERY,
    INLINE;

    public static final CalculatorType CURRENT =
            CalculatorType.valueOf(BotProperties.MODULES.getString("calculator.type"));

    public boolean isCurrent() {
        return this.equals(CURRENT);
    }
}
