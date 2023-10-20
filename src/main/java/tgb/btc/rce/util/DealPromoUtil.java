package tgb.btc.rce.util;

import tgb.btc.rce.enums.FirstDealPromoType;
import tgb.btc.rce.enums.properties.BotProperties;

public final class DealPromoUtil {
    private DealPromoUtil() {
    }

    public static boolean isNone() {
        return FirstDealPromoType.NONE.equals(FirstDealPromoType.valueOf(
                BotProperties.MODULES.getString("first.deal.promo.type")));
    }
}
