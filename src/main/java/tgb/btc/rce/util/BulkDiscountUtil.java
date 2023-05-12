package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.util.StringUtils;
import tgb.btc.rce.constants.FilePaths;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.vo.BulkDiscount;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public final class BulkDiscountUtil {

    private static List<BulkDiscount> BULK_DISCOUNTS = new ArrayList<>();

    private BulkDiscountUtil() {
    }

    public static void load() throws PropertyValueNotFoundException {
        for (String key : BotProperties.BULK_DISCOUNT_PROPERTIES.getKeys()) {
            int sum = Integer.parseInt(key);
            Double percent = BotProperties.BULK_DISCOUNT_PROPERTIES.getDouble(key);
            if (Objects.isNull(percent)) throw new PropertyValueNotFoundException("Не указано значение для суммы " + key + ".");
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
