package tgb.btc.rce.enums;

public enum CalculatorType {
    NONE,
    INLINE_QUERY,
    INLINE;

    public static final CalculatorType CURRENT =
            CalculatorType.valueOf(BotProperties.MODULES_PROPERTIES.getString("calculator.type"));

    public boolean isCurrent() {
        return this.equals(CURRENT);
    }
}
