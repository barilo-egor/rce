package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.vo.BulkDiscount;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public final class BulkDiscountUtil {

    private static List<BulkDiscount> BULK_DISCOUNTS = new ArrayList<>();

    private BulkDiscountUtil() {
    }

    public static void load() {
        PropertiesConfiguration properties;
        try {
            properties = new PropertiesConfiguration(FilePaths.BULK_DISCOUNT_PROPERTIES);
        } catch (ConfigurationException e) {
            log.error("Ошибка при загрузке оптовых скидок: " + e.getMessage() + "\n" + ExceptionUtils.getFullStackTrace(e));
            return;
        }
        List<String> keys = new ArrayList<>();
        for (Iterator<String> it = properties.getKeys(); it.hasNext(); ) {
            keys.add(it.next());
        }

        for (String key : keys) {
            int sum = Integer.parseInt(key);
            double percent = properties.getDouble(key);
            BULK_DISCOUNTS.add(BulkDiscount.builder()
                            .sum(sum)
                            .percent(percent)
                    .build());
        }
        BULK_DISCOUNTS.sort(Comparator.comparingInt(BulkDiscount::getSum));
        Collections.reverse(BULK_DISCOUNTS);
        System.out.println();
    }

    public static List<BulkDiscount> getBulkDiscounts() {
        return new ArrayList<>(BULK_DISCOUNTS);
    }

    public static BigDecimal getPercentBySum(BigDecimal sum) {
        for (BulkDiscount bulkDiscount : BULK_DISCOUNTS) {
            if (BigDecimal.valueOf(bulkDiscount.getSum()).compareTo(sum) < 1)
                return BigDecimal.valueOf(bulkDiscount.getPercent());
        }
        return BigDecimal.ZERO;
    }
}
