package tgb.btc.rce.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.exception.PropertyValueNotFoundException;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.vo.BulkDiscount;
import tgb.btc.web.constant.ControllerMapping;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

import static tgb.btc.rce.util.BulkDiscountUtil.BULK_DISCOUNTS;

@Controller
@RequestMapping(ControllerMapping.BULK_DISCOUNTS)
public class BulkDiscountController {

    @GetMapping(value = "/getDiscounts")
    @ResponseBody
    public ObjectNode getDiscounts() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode fiatCurrencies = objectMapper.createArrayNode();
        for (FiatCurrency fiatCurrencyEnum : FiatCurrencyUtil.getFiatCurrencies()) {
            ObjectNode fiatCurrency = objectMapper.createObjectNode();
            fiatCurrency.put("name", fiatCurrencyEnum.name());
            ArrayNode dealTypes = objectMapper.createArrayNode();
            for (DealType dealTypeEnum : DealType.values()) {
                ObjectNode dealType = objectMapper.createObjectNode();
                dealType.put("name", dealTypeEnum.name());
                dealType.put("displayName", StringUtils.capitalize(dealTypeEnum.getNominative()));
                ArrayNode bulkDiscounts = objectMapper.createArrayNode();
                for (BulkDiscount bulkDiscountVo : BULK_DISCOUNTS.stream()
                        .filter(bulkDiscount -> bulkDiscount.getFiatCurrency().equals(fiatCurrencyEnum))
                        .filter(bulkDiscount -> bulkDiscount.getDealType().equals(dealTypeEnum))
                        .collect(Collectors.toList())) {
                    ObjectNode bulkDiscount = objectMapper.createObjectNode();
                    bulkDiscount.put("sum", bulkDiscountVo.getSum());
                    bulkDiscount.put("percent", bulkDiscountVo.getPercent());
                    bulkDiscounts.add(bulkDiscount);
                }
                dealType.set("bulkDiscounts", bulkDiscounts);
                dealTypes.add(dealType);
            }
            fiatCurrency.set("dealTypes", dealTypes);
            fiatCurrencies.add(fiatCurrency);
        }
        ObjectNode result = objectMapper.createObjectNode();
        result.put("success", true);
        result.set("data", fiatCurrencies);
        return result;
    }

    @PostMapping(value = "/saveDiscount")
    @ResponseBody
    public ObjectNode saveDiscount(
            @RequestBody BulkDiscount bulkDiscount, @RequestParam(required = false) Integer oldSum) {
        String key = String.join(".", new String[]{bulkDiscount.getFiatCurrency().getCode(),
                bulkDiscount.getDealType().getKey(), String.valueOf(bulkDiscount.getSum())});
        if (Objects.nonNull(oldSum) && !oldSum.equals(bulkDiscount.getSum())) {
            String oldKey = String.join(".", new String[]{bulkDiscount.getFiatCurrency().getCode(),
                    bulkDiscount.getDealType().getKey(), String.valueOf(oldSum)});
            CommonProperties.BULK_DISCOUNT.clearProperty(oldKey);
        }
        CommonProperties.BULK_DISCOUNT.setProperty(key, String.valueOf(bulkDiscount.getPercent()));
        reload();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("success", true);
        return result;
    }

    @DeleteMapping(value = "/removeDiscount")
    @ResponseBody
    public ObjectNode removeDiscount(
            @RequestBody BulkDiscount bulkDiscount) {
        String key = String.join(".", new String[]{bulkDiscount.getFiatCurrency().getCode(),
                bulkDiscount.getDealType().getKey(), String.valueOf(bulkDiscount.getSum())});
        CommonProperties.BULK_DISCOUNT.clearProperty(key);
        reload();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("success", true);
        return result;
    }

    public void reload() {
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
