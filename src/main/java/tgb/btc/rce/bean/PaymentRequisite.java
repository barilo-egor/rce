package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;

@Entity
@Table(name = "PAYMENT_REQUISITE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequisite extends BasePersist {

    @ManyToOne
    private PaymentType paymentType;

    @Column(name = "REQUISITE")
    private String requisite;

    @Column(name = "ORDER")
    private Integer order;

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

}
