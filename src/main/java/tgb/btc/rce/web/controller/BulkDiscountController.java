package tgb.btc.rce.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.enums.BotProperties;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.service.impl.BulkDiscountService;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.vo.BulkDiscount;

import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/bulk_discount")
public class BulkDiscountController {

    @GetMapping(value = "/getDiscounts")
    @ResponseBody
    public ObjectNode getDiscounts() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode fiatCurrencies = objectMapper.createArrayNode();
        for (FiatCurrency fiatCurrencyEnum : FiatCurrencyUtil.getFiatCurrencies()) {
            ObjectNode fiatCurrency = objectMapper.createObjectNode();
            fiatCurrency.put("displayName", fiatCurrencyEnum.name());
            ArrayNode dealTypes = objectMapper.createArrayNode();
            for (DealType dealTypeEnum : DealType.values()) {
                ObjectNode dealType = objectMapper.createObjectNode();
                dealType.put("displayName", dealTypeEnum.name());
                ArrayNode bulkDiscounts = objectMapper.createArrayNode();
                for (BulkDiscount bulkDiscountVo : BulkDiscountService.BULK_DISCOUNTS.stream()
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
            BotProperties.BULK_DISCOUNT.clearProperty(oldKey);
        }
        BotProperties.BULK_DISCOUNT.setProperty(key, String.valueOf(bulkDiscount.getPercent()));
        BotProperties.BULK_DISCOUNT.load();
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
        BotProperties.BULK_DISCOUNT.clearProperty(key);
        BotProperties.BULK_DISCOUNT.load();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode result = objectMapper.createObjectNode();
        result.put("success", true);
        return result;
    }

}