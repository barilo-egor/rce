package tgb.btc.rce.enums;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.Arrays;
import java.util.function.Function;

public enum DealType implements ObjectNodeConvertable<DealType> {
    BUY("покупка", "покупку", "покупки", "buy"),
    SELL("продажа", "продажу", "продажи", "sell");

    /**
     * Именительный
     */
    final String nominative;
    /**
     * Родительный
     */
    final String genitive;
    /**
     * Винительный
     */
    final String accusative;


    final String key;

    DealType(String nominative, String genitive, String accusative, String key) {
        this.nominative = nominative;
        this.genitive = genitive;
        this.accusative = accusative;
        this.key = key;
    }

    public String getNominative() {
        return nominative;
    }

    public String getGenitive() {
        return genitive;
    }

    public String getNominativeFirstLetterToUpper() {
        String firstLetter = nominative.substring(0, 1).toUpperCase();
        return firstLetter + nominative.substring(1);
    }

    public String getAccusative() {
        return accusative;
    }

    public String getKey() {
        return key;
    }

    public static boolean isBuy(DealType dealType) {
        return DealType.BUY.equals(dealType);
    }

    public static DealType findByKey(String key) {
        return Arrays.stream(DealType.values())
                .filter(dealType -> dealType.getKey().equals(key))
                .findFirst()
                .orElseThrow(EnumTypeNotFoundException::new);
    }

    @Override
    public Function<DealType, ObjectNode> mapFunction() {
        return dealType -> JacksonUtil.getEmpty()
                .put("name", dealType.name())
                .put("nominative", dealType.getNominative());
    }
}
