package tgb.btc.rce.util;

import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.service.impl.ReviewPriseService;
import tgb.btc.rce.vo.ReviewPrise;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class ReviewPriseUtil {

    public static ReviewPrise getReviewPrise(BigDecimal sum, FiatCurrency fiatCurrency) {
        for (ReviewPrise reviewPrise : ReviewPriseService.REVIEW_PRISES.stream()
                .filter(reviewPrise -> reviewPrise.getFiatCurrency().equals(fiatCurrency))
                .collect(Collectors.toList())) {
            if (BigDecimal.valueOf(reviewPrise.getSum()).compareTo(sum) < 1)
                return reviewPrise;
        }
        return null;
    }


}
