package tgb.btc.rce.bean;

import lombok.Builder;
import tgb.btc.rce.enums.PaymentType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "PAYMENT_CONFIG")
@Builder
public class PaymentConfig extends BasePersist {

    @Column(name = "PAYMENT_TYPE", unique = true)
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "IS_ON")
    private Boolean isOn;

    @Column(name = "REQUISITES", length = 500)
    private String requisites;

    public PaymentConfig() {
    }

    public PaymentConfig(PaymentType paymentType, Boolean isOn, String requisites) {
        this.paymentType = paymentType;
        this.isOn = isOn;
        this.requisites = requisites;
    }

    public String getRequisites() {
        return requisites;
    }

    public void setRequisites(String requisites) {
        this.requisites = requisites;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public Boolean getOn() {
        return isOn;
    }

    public void setOn(Boolean on) {
        isOn = on;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PaymentConfig that = (PaymentConfig) o;
        return paymentType == that.paymentType && Objects.equals(isOn, that.isOn) && Objects.equals(requisites, that.requisites);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), paymentType, isOn, requisites);
    }

    @Override
    public String toString() {
        return "PaymentConfig{" +
                "paymentType=" + paymentType +
                ", isOn=" + isOn +
                ", requisites='" + requisites + '\'' +
                '}';
    }
}
