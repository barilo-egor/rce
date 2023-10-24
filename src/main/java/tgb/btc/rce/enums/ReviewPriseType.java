package tgb.btc.rce.enums;

import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.interfaces.Module;

public enum ReviewPriseType implements Module {
    STANDARD,
    DYNAMIC;

    public static final ReviewPriseType CURRENT =
            ReviewPriseType.valueOf(CommonProperties.MODULES.getString("review.prise"));

    @Override
    public boolean isCurrent () {
        return this.equals(CURRENT);
    }
}
