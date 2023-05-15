package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.service.impl.BulkDiscountValidateService;
import tgb.btc.rce.vo.BulkDiscount;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public final class BulkDiscountUtil {

    private BulkDiscountUtil() {
    }

    public static List<BulkDiscount> getBulkDiscounts() {
        return new ArrayList<>(BulkDiscountValidateService.BULK_DISCOUNTS);
    }

    public static BigDecimal getPercentBySum(BigDecimal sum) {
        for (BulkDiscount bulkDiscount : BulkDiscountValidateService.BULK_DISCOUNTS) {
            if (BigDecimal.valueOf(bulkDiscount.getSum()).compareTo(sum) < 1)
                return BigDecimal.valueOf(bulkDiscount.getPercent());
        }
        return BigDecimal.ZERO;
    }

}
