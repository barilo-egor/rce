package tgb.btc.lib.util;

import tgb.btc.lib.enums.BotProperties;
import tgb.btc.lib.enums.FirstDealPromoType;

public final class DealPromoUtil {
    private DealPromoUtil() {
    }

    public static boolean isNone() {
        return FirstDealPromoType.NONE.equals(FirstDealPromoType.valueOf(
                BotProperties.MODULES_PROPERTIES.getString("first.deal.promo.type")));
    }
}
