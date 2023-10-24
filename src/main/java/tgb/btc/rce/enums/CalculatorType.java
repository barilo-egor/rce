package tgb.btc.rce.enums;

import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.interfaces.Module;

public enum CalculatorType implements Module {
    NONE,
    INLINE_QUERY,
    INLINE;

    public static final CalculatorType CURRENT =
            CalculatorType.valueOf(CommonProperties.MODULES.getString("calculator.type"));

    public boolean isCurrent() {
        return this.equals(CURRENT);
    }
}
