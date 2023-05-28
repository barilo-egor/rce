package tgb.btc.rce.enums;

import tgb.btc.rce.exception.EnumTypeNotFoundException;

import java.util.Arrays;

public enum DealType {
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
}
