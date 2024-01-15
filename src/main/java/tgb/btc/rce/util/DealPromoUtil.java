package tgb.btc.rce.util;

import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.rce.enums.FirstDealPromoType;

public final class DealPromoUtil {
    private DealPromoUtil() {
    }

    public static boolean isNone() {
        return FirstDealPromoType.NONE.equals(FirstDealPromoType.valueOf(
                PropertiesPath.MODULES_PROPERTIES.getString("first.deal.promo.type")));
    }
}
