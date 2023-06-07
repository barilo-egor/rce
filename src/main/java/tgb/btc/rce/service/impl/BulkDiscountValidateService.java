package tgb.btc.rce.service.impl;

import org.apache.commons.lang.StringUtils;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.exception.PropertyValueNotFoundException;
import tgb.btc.rce.service.IValidateService;
import tgb.btc.rce.vo.BulkDiscount;

import java.util.*;

public class BulkDiscountValidateService implements IValidateService {

    public static final List<BulkDiscount> BULK_DISCOUNTS = new ArrayList<>();

    @Override
    public void validate(BotProperties botProperties)  throws PropertyValueNotFoundException {
        BULK_DISCOUNTS.clear();
        for (String key : botProperties.getKeys()) {
            int sum;
            if (StringUtils.isBlank(key)) {
                throw new PropertyValueNotFoundException("Не указано название для одного из ключей" + key + ".");
            }
            try {
                sum = Integer.parseInt(key.split("\\.")[1]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное название для ключа " + key + ".");
            }
            String value = botProperties.getString(key);
            if (StringUtils.isBlank(value)) {
                throw new PropertyValueNotFoundException("Не указано значение для ключа " + key + ".");
            }
            double percent;
            try {
                percent = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное значение для ключа " + key + ".");
            }
            BULK_DISCOUNTS.add(BulkDiscount.builder()
                            .percent(percent)
                            .sum(sum)
                            .fiatCurrency(FiatCurrency.getByCode(key.split("\\.")[0]))
                    .build());
        }
        BULK_DISCOUNTS.sort(Comparator.comparingInt(BulkDiscount::getSum));
        Collections.reverse(BULK_DISCOUNTS);
    }

    @Override
    public void load() {
        BULK_DISCOUNTS.clear();
        for (String key : BotProperties.BULK_DISCOUNT_PROPERTIES.getKeys()) {
            int sum;
            if (StringUtils.isBlank(key)) {
                throw new PropertyValueNotFoundException("Не указано название для одного из ключей" + key + ".");
            }
            try {
                sum = Integer.parseInt(key.split("\\.")[1]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное название для ключа " + key + ".");
            }
            String value =  BotProperties.BULK_DISCOUNT_PROPERTIES.getString(key);
            if (StringUtils.isBlank(value)) {
                throw new PropertyValueNotFoundException("Не указано значение для ключа " + key + ".");
            }
            double percent;
            try {
                percent = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное значение для ключа " + key + ".");
            }
            BULK_DISCOUNTS.add(BulkDiscount.builder()
                    .percent(percent)
                    .sum(sum)
                    .fiatCurrency(FiatCurrency.getByCode(key.split("\\.")[0]))
                    .build());
        }
        BULK_DISCOUNTS.sort(Comparator.comparingInt(BulkDiscount::getSum));
        Collections.reverse(BULK_DISCOUNTS);
    }

}
