package tgb.btc.rce.enums;

public enum ReviewPriseType {
    STANDARD,
    DYNAMIC;

    public static final ReviewPriseType CURRENT =
            ReviewPriseType.valueOf(BotProperties.MODULES.getString("review.prise"));

    public boolean isCurrent () {
        return this.equals(CURRENT);
    }
}
