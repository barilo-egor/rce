package tgb.btc.lib.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tgb.btc.lib.enums.ReceiptFormat;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "PAYMENT_RECEIPT")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentReceipt extends BasePersist {

    @Column(name = "RECEIPT", length = 1000)
    private String receipt;

    @Column(name = "RECEIPT_FORMAT")
    @Enumerated(EnumType.STRING)
    private ReceiptFormat receiptFormat;

    @ManyToOne
    private Deal deal;

    public Deal getDeal() {
        return deal;
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public ReceiptFormat getReceiptFormat() {
        return receiptFormat;
    }

    public void setReceiptFormat(ReceiptFormat receiptFormat) {
        this.receiptFormat = receiptFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentReceipt that = (PaymentReceipt) o;
        return Objects.equals(receipt, that.receipt) && receiptFormat == that.receiptFormat;
    }

    @Override
    public int hashCode() {
        return Objects.hash(receipt, receiptFormat);
    }

    @Override
    public String toString() {
        return "PaymentReceipt{" +
                "receipt='" + receipt + '\'' +
                ", receiptFormat=" + receiptFormat +
                '}';
    }
}
