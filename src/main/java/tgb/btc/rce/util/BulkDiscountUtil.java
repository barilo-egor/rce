package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.vo.BulkDiscount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class BulkDiscountUtil {

    public static final List<BulkDiscount> BULK_DISCOUNTS = new ArrayList<>();

    private BulkDiscountUtil() {
    }

    public static BigDecimal getPercentBySum(BigDecimal sum, FiatCurrency fiatCurrency, DealType dealType) {
        for (BulkDiscount bulkDiscount : BULK_DISCOUNTS.stream()
                .filter(bulkDiscount -> bulkDiscount.getFiatCurrency().equals(fiatCurrency)
                        && bulkDiscount.getDealType().equals(dealType))
                .collect(Collectors.toList())) {
            if (BigDecimal.valueOf(bulkDiscount.getSum()).compareTo(sum) < 1)
                return BigDecimal.valueOf(bulkDiscount.getPercent());
        }
        return BigDecimal.ZERO;
    }

}
