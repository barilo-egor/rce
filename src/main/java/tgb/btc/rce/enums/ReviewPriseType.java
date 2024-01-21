package tgb.btc.rce.enums;

import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.interfaces.Module;

public enum ReviewPriseType implements Module {
    STANDARD,
    DYNAMIC;

    public static final ReviewPriseType CURRENT =
            ReviewPriseType.valueOf(PropertiesPath.MODULES_PROPERTIES.getString("review.prise"));

    @Override
    public boolean isCurrent() {
        return this.equals(CURRENT);
    }
}
