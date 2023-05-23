package tgb.btc.rce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tgb.btc.rce.bean.Deal;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DealAmount {

    private BigDecimal amount;

    private BigDecimal cryptoAmount;

    private BigDecimal commission;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(BigDecimal cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public void updateDeal(Deal deal) {
        deal.setAmount(amount);
        deal.setCryptoAmount(cryptoAmount);
        deal.setCommission(commission);
        deal.setOriginalPrice(amount);
    }
}
