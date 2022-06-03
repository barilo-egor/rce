package tgb.btc.rce.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
}