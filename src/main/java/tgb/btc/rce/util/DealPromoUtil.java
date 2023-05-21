package tgb.btc.rce.util;

import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.FirstDealPromoType;

public final class DealPromoUtil {
    private DealPromoUtil() {
    }

    public static boolean isNone() {
        return FirstDealPromoType.NONE.equals(FirstDealPromoType.valueOf(
                BotProperties.MODULES_PROPERTIES.getString("first.deal.promo.type")));
    }
}
