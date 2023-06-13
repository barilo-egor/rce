package tgb.btc.rce.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDealVO {
    private Long pid;

    private Long userPid;

    private DealType dealType;

    private CryptoCurrency cryptoCurrency;

    private BigDecimal cryptoAmount;

    private FiatCurrency fiatCurrency;

    private BigDecimal amount;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Long getUserPid() {
        return userPid;
    }

    public void setUserPid(Long userPid) {
        this.userPid = userPid;
    }

    public DealType getDealType() {
        return dealType;
    }

    public void setDealType(DealType dealType) {
        this.dealType = dealType;
    }

    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(BigDecimal cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }

    public FiatCurrency getFiatCurrency() {
        return fiatCurrency;
    }

    public void setFiatCurrency(FiatCurrency fiatCurrency) {
        this.fiatCurrency = fiatCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
