package tgb.btc.rce.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class BigDecimalUtil {
    private BigDecimalUtil() {
    }

    public static final int scale = 10;

    public static BigDecimal divideHalfUp(BigDecimal a, BigDecimal b) {
        return a.setScale(scale, RoundingMode.HALF_UP).divide(b, RoundingMode.HALF_UP);
    }

    public static BigDecimal multiplyHalfUp(BigDecimal a, BigDecimal b) {
        return a.setScale(scale, RoundingMode.HALF_UP).multiply(b);
    }

    public static BigDecimal addHalfUp(BigDecimal a, BigDecimal b) {
        return a.setScale(scale, RoundingMode.HALF_UP).add(b);
    }

    public static BigDecimal subtractHalfUp(BigDecimal a, BigDecimal b) {
        return a.setScale(scale, RoundingMode.HALF_UP).subtract(b);
    }

    public static BigDecimal roundNullSafe(BigDecimal num, int scale) {
        if (Objects.isNull(num)) return BigDecimal.valueOf(0);
        return num.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static BigDecimal round(BigDecimal num, int scale) {
        return num.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static BigDecimal round(Double num, int scale) {
        return BigDecimal.valueOf(num).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }
}