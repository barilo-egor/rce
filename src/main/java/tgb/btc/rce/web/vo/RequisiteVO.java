package tgb.btc.rce.web.vo;

import lombok.Getter;
import lombok.Setter;

public class RequisiteVO {

    @Getter
    @Setter
    private Long pid;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String requisite;

    private Boolean isOn;

    public Boolean getIsOn() {
        return isOn;
    }

    public void setIsOn(Boolean on) {
        isOn = on;
    }
}
