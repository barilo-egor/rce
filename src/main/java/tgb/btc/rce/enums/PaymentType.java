package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

public enum PaymentType {
    RF_CARD("\uD83D\uDCB3 Карта РФ СБП"),
    QIWI("\uD83D\uDD35 QIWI"),
    TINKOFF("\uD83D\uDFE1 Tinkoff"),
    SBERBANK("\uD83D\uDFE2 Сбербанк");

    final String displayName;

    PaymentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentType fromDisplayName(String displayName) {
        for(PaymentType paymentType : PaymentType.values()){
            if(paymentType.getDisplayName().equals(displayName)) return paymentType;
        }
        throw new BaseException("Не найден тип оплаты.");
    }
}
