package tgb.btc.rce.util;

import org.apache.commons.lang.StringUtils;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.exception.PropertyValueNotFoundException;
import tgb.btc.rce.vo.ReviewPrise;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewPriseUtil {

    public static final List<ReviewPrise> REVIEW_PRISES = new ArrayList<>();

    static {
        load();
    }

    public static ReviewPrise getReviewPrise(BigDecimal sum, FiatCurrency fiatCurrency) {
        for (ReviewPrise reviewPrise : REVIEW_PRISES.stream()
                .filter(reviewPrise -> reviewPrise.getFiatCurrency().equals(fiatCurrency))
                .collect(Collectors.toList())) {
            if (BigDecimal.valueOf(reviewPrise.getSum()).compareTo(sum) < 1)
                return reviewPrise;
        }
        return null;
    }

    public static void load() {
        REVIEW_PRISES.clear();
        for (String key : PropertiesPath.REVIEW_PRISE_PROPERTIES.getKeys()) {
            int sum;
            if (StringUtils.isBlank(key)) {
                throw new PropertyValueNotFoundException("Не указано название для одного из ключей" + key + ".");
            }
            try {
                sum = Integer.parseInt(key.split("\\.")[1]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное название для ключа " + key + ".");
            }
            String[] priseValues = PropertiesPath.REVIEW_PRISE_PROPERTIES.getStringArray(key);
            if (priseValues.length == 0) {
                throw new PropertyValueNotFoundException("Не указано значение для ключа " + key + ".");
            }
            int minPrise;
            int maxPrise;
            try {
                minPrise = Integer.parseInt(priseValues[0]);
                maxPrise = Integer.parseInt(priseValues[1]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное значение для ключа " + key + ".");
            }
            REVIEW_PRISES.add(ReviewPrise.builder()
                    .minPrise(minPrise)
                    .maxPrise(maxPrise)
                    .sum(sum)
                    .fiatCurrency(FiatCurrency.getByCode(key.split("\\.")[0]))
                    .build());
        }
        REVIEW_PRISES.sort(Comparator.comparingInt(ReviewPrise::getSum));
        Collections.reverse(REVIEW_PRISES);
    }
}
