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

    private CalculateData calculateData;

    private BigDecimal amount;

    private BigDecimal cryptoAmount;

    private BigDecimal commission;

    private boolean isEnteredInCrypto;

    public boolean isEnteredInCrypto() {
        return isEnteredInCrypto;
    }

    public void setEnteredInCrypto(boolean enteredInCrypto) {
        isEnteredInCrypto = enteredInCrypto;
    }

    public CalculateData getCalculateData() {
        return calculateData;
    }

    public void setCalculateData(CalculateData calculateData) {
        this.calculateData = calculateData;
    }

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
