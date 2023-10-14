package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "PAYMENT_REQUISITE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequisite extends BasePersist {

    @ManyToOne
    private PaymentType paymentType;

    @Column(name = "NAME")
    private String name;

    @Column(name = "REQUISITE")
    private String requisite;

    @Column(name = "REQUISITE_ORDER")
    private Integer requisiteOrder;

    @Column(name = "IS_ON")
    private Boolean isOn;

    public PaymentRequisite(Long pid) {
        super(pid);
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getRequisite() {
        return requisite;
    }

    public void setRequisite(String requisite) {
        this.requisite = requisite;
    }

    public Integer getRequisiteOrder() {
        return requisiteOrder;
    }

    public void setRequisiteOrder(Integer order) {
        this.requisiteOrder = order;
    }

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
}
