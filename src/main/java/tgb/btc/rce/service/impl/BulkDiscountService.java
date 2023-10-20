package tgb.btc.rce.service.impl;

import org.apache.commons.lang.StringUtils;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.exception.PropertyValueNotFoundException;
import tgb.btc.rce.enums.properties.CommonProperties;
import tgb.btc.rce.service.IPropertyService;
import tgb.btc.rce.vo.BulkDiscount;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BulkDiscountService implements IPropertyService {

    // TODO вынести поле отсюда, удалить этот сервис, интерфейс
    public static final List<BulkDiscount> BULK_DISCOUNTS = new ArrayList<>();

    @Override
    public void load() {
        BULK_DISCOUNTS.clear();
        for (String key : CommonProperties.BULK_DISCOUNT.getKeys()) {
            int sum;
            if (StringUtils.isBlank(key)) {
                throw new PropertyValueNotFoundException("Не указано название для одного из ключей" + key + ".");
            }
            try {
                sum = Integer.parseInt(key.split("\\.")[2]);
            } catch (NumberFormatException e) {
                throw new PropertyValueNotFoundException("Не корректное название для ключа " + key + ".");
            }
            String value =  CommonProperties.BULK_DISCOUNT.getString(key);
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
                    .dealType(DealType.findByKey((key.split("\\.")[1])))
                    .build());
        }
        BULK_DISCOUNTS.sort(Comparator.comparingInt(BulkDiscount::getSum));
        Collections.reverse(BULK_DISCOUNTS);
    }

}
