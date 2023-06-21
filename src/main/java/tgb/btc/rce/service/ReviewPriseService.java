package tgb.btc.rce.service;

import org.apache.commons.lang.StringUtils;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.vo.ReviewPrise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReviewPriseService implements IValidateService{

    public static final List<ReviewPrise> REVIEW_PRISES = new ArrayList<>();

    @Override
    public void load() {
        REVIEW_PRISES.clear();
        for (String key : BotProperties.REVIEW_PRISE.getKeys()) {
            int sum;
            if (StringUtils.isBlank(key)) {
                throw new PropertyValueNotFoundException("Не указано название для одного из ключей" + key + ".");
            }
            try {
                sum = Integer.parseInt(key.split("\\.")[1]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное название для ключа " + key + ".");
            }
            String value =  BotProperties.REVIEW_PRISE.getString(key);
            if (StringUtils.isBlank(value)) {
                throw new PropertyValueNotFoundException("Не указано значение для ключа " + key + ".");
            }
            int minPrise;
            int maxPrise;
            try {
                String[] priseValues = StringUtils.split(value, ';');
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
