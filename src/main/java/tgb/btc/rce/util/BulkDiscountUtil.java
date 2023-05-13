package tgb.btc.rce.util;

import lombok.extern.slf4j.Slf4j;
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

    public static void load(Map<Integer, Double> discounts) throws PropertyValueNotFoundException {
        if (Objects.isNull(discounts)) {
            discounts = validate(BotProperties.BULK_DISCOUNT_PROPERTIES);
        }
        BULK_DISCOUNTS.clear();
        discounts.forEach((sum, percent) -> BULK_DISCOUNTS.add(BulkDiscount.builder()
                                                                       .sum(sum)
                                                                       .percent(percent)
                                                                       .build()));
        BULK_DISCOUNTS.sort(Comparator.comparingInt(BulkDiscount::getSum));
        Collections.reverse(BULK_DISCOUNTS);
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

    public static Map<Integer, Double> validate(BotProperties botProperties) throws PropertyValueNotFoundException{
        Map<Integer, Double> discounts = new HashMap<>();
        for (String key : botProperties.getKeys()) {
            Integer sum = Integer.parseInt(key);
            Double percent = botProperties.getDouble(key);
            if (Objects.isNull(percent)) {
                throw new PropertyValueNotFoundException("Не корректно указано значение для суммы " + key + ".");
            }
            discounts.put(sum, percent);
        }
        return discounts;
    }

}
