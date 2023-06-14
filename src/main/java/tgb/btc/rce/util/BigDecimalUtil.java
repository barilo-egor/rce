package tgb.btc.rce.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class BigDecimalUtil {
    private BigDecimalUtil() {
    }

    public static final int scale = 20;

    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

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

    public static String roundToPlainString(BigDecimal num, int scale) {
        return toPlainString(round(num, scale));
    }

    public static String roundToPlainString(BigDecimal num) {
        return roundToPlainString(num, 0);
    }


    public static String toPlainString(BigDecimal num) {
        return num.stripTrailingZeros().toPlainString();
    }

    public static BigDecimal round(Double num, int scale) {
        return BigDecimal.valueOf(num).setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static boolean isZero(BigDecimal number) {
        return BigDecimal.ZERO.compareTo(number) == 0;
    }
}