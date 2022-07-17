package tgb.btc.rce.bean;

import lombok.Builder;
import tgb.btc.rce.enums.ReceiptFormat;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "PAYMENT_RECEIPT")
@Builder
public class PaymentReceipt extends BasePersist {

    @Column(name = "RECEIPT", length = 1000)
    private String receipt;

    @Column(name = "RECEIPT_FORMAT")
    @Enumerated(EnumType.STRING)
    private ReceiptFormat receiptFormat;

    public PaymentReceipt() {
    }

    public PaymentReceipt(String receipt, ReceiptFormat receiptFormat) {
        this.receipt = receipt;
        this.receiptFormat = receiptFormat;
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
