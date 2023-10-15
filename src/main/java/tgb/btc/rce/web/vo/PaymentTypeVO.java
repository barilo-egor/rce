package tgb.btc.rce.web.vo;

import lombok.Getter;
import lombok.Setter;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;

import java.math.BigDecimal;
import java.util.List;

public class PaymentTypeVO {
    @Getter
    @Setter
    private Long pid;

    @Getter
    @Setter
    private String name;

    private Boolean isOn;

    @Getter
    @Setter
    private FiatCurrency fiatCurrency;

    @Getter
    @Setter
    private DealType dealType;

    @Getter
    @Setter
    private BigDecimal minSum;

    @Getter
    @Setter
    private List<RequisiteVO> requisites;

    private Boolean isDynamicOn;

    public Boolean getIsOn() {
        return isOn;
    }

    public void setIsOn(Boolean on) {
        isOn = on;
    }

    public Boolean getIsDynamicOn() {
        return isDynamicOn;
    }

    public void setIsDynamicOn(Boolean dynamicOn) {
        isDynamicOn = dynamicOn;
    }
}
