package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.service.impl.BulkDiscountValidateService;
import tgb.btc.rce.vo.BulkDiscount;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Slf4j
public final class BulkDiscountUtil {

    private BulkDiscountUtil() {
    }

    public static BigDecimal getPercentBySum(BigDecimal sum, FiatCurrency fiatCurrency) {
        for (BulkDiscount bulkDiscount : BulkDiscountValidateService.BULK_DISCOUNTS.stream()
                .filter(bulkDiscount -> bulkDiscount.getFiatCurrency().equals(fiatCurrency))
                .collect(Collectors.toList())) {
            if (BigDecimal.valueOf(bulkDiscount.getSum()).compareTo(sum) < 1)
                return BigDecimal.valueOf(bulkDiscount.getPercent());
        }
        return BigDecimal.ZERO;
    }

}
