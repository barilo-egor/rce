package tgb.btc.rce.enums;

import tgb.btc.rce.enums.properties.BotProperties;
import tgb.btc.rce.service.Module;

public enum ReviewPriseType implements Module {
    STANDARD,
    DYNAMIC;

    public static final ReviewPriseType CURRENT =
            ReviewPriseType.valueOf(BotProperties.MODULES.getString("review.prise"));

    @Override
    public boolean isCurrent () {
        return this.equals(CURRENT);
    }
}
