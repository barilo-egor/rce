package tgb.btc.rce.enums;

import tgb.btc.rce.exception.BaseException;

public enum DeliveryType {
    ROCKET("\uD83D\uDE80 Приоритет"),
    NORMAL("\uD83D\uDE9C Обычная");

    final String deliveryType;

    DeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public static DeliveryType fromString(String deliveryType){
        for(DeliveryType delveryType : DeliveryType.values()){
            if(delveryType.getDeliveryType().equals(deliveryType)) return delveryType;
        }
        throw new BaseException("Не найден тип доставки.");
    }
}
