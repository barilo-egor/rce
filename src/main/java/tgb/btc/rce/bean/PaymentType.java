package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tgb.btc.rce.enums.DealType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;

@Entity
@Table(name = "PAYMENT_TYPE", uniqueConstraints = @UniqueConstraint(columnNames = {"NAME", "DEAL_TYPE"}))
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentType extends BasePersist {

    @Column(name = "NAME")
    private String name;

    @Column(name = "IS_ON")
    private Boolean isOn;

    @Column(name = "MIN_SUM")
    private BigDecimal minSum;

    @Column(name = "DEAL_TYPE")
    private DealType dealType;

    @Column(name = "IS_DYNAMIC_ON")
    private Boolean isDynamicOn;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOn() {
        return isOn;
    }

    public void setOn(Boolean on) {
        isOn = on;
    }

    public BigDecimal getMinSum() {
        return minSum;
    }

    public void setMinSum(BigDecimal minSum) {
        this.minSum = minSum;
    }

    public DealType getDealType() {
        return dealType;
    }

    public void setDealType(DealType dealType) {
        this.dealType = dealType;
    }

    public Boolean getDynamicOn() {
        return isDynamicOn;
    }

    public void setDynamicOn(Boolean dynamicOn) {
        isDynamicOn = dynamicOn;
    }

}
