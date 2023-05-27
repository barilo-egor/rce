package tgb.btc.rce.enums;

import tgb.btc.rce.exception.EnumTypeNotFoundException;

import java.util.Arrays;

public enum DealType {
    BUY("покупка", "покупку", "buy"),
    SELL("продажа", "продажу", "sell");

    /**
     * Именительный
     */
    final String nominative;
    /**
     * Винительный
     */
    final String accusative;


    final String key;

    DealType(String nominative, String accusative, String key) {
        this.nominative = nominative;
        this.accusative = accusative;
        this.key = key;
    }

    public String getNominative() {
        return nominative;
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
