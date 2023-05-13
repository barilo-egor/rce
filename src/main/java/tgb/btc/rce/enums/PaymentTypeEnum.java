package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

public enum PaymentTypeEnum {
    RF_CARD("\uD83D\uDCB3 Карта РФ СБП"),
    QIWI("\uD83D\uDD35 QIWI"),
    TINKOFF("\uD83D\uDFE1 Tinkoff"),
    SBERBANK("\uD83D\uDFE2 Сбербанк"),
    BY_CARD("Карта РБ"),
    UAH_CARD("Карта Украина");

    final String displayName;

    PaymentTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentTypeEnum fromDisplayName(String displayName) {
        for(PaymentTypeEnum paymentTypeEnum : PaymentTypeEnum.values()){
            if(paymentTypeEnum.getDisplayName().equals(displayName)) return paymentTypeEnum;
        }
        throw new BaseException("Не найден тип оплаты.");
    }
}
