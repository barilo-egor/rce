package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tgb.btc.rce.enums.ApiDealStatus;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "API_DEAL")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiDeal extends BasePersist{

    @ManyToOne(fetch = FetchType.EAGER)
    private ApiUser apiUser;

    private LocalDateTime dateTime;

    private DealType dealType;

    private BigDecimal cryptoAmount;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private ApiDealStatus apiDealStatus;

    private CryptoCurrency cryptoCurrency;

    private String requisite;

    public ApiUser getApiUser() {
        return apiUser;
    }

    public void setApiUser(ApiUser apiUser) {
        this.apiUser = apiUser;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public DealType getDealType() {
        return dealType;
    }

    public void setDealType(DealType dealType) {
        this.dealType = dealType;
    }

    public BigDecimal getCryptoAmount() {
        return cryptoAmount;
    }

    public void setCryptoAmount(BigDecimal cryptoAmount) {
        this.cryptoAmount = cryptoAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ApiDealStatus getApiDealStatus() {
        return apiDealStatus;
    }

    public void setApiDealStatus(ApiDealStatus apiDealStatus) {
        this.apiDealStatus = apiDealStatus;
    }

    public CryptoCurrency getCryptoCurrency() {
        return cryptoCurrency;
    }

    public void setCryptoCurrency(CryptoCurrency cryptoCurrency) {
        this.cryptoCurrency = cryptoCurrency;
    }

    public String getRequisite() {
        return requisite;
    }

    public void setRequisite(String requisite) {
        this.requisite = requisite;
    }

    public BigDecimal getAmountToPay() {
        if (DealType.isBuy(dealType)) return amount;
        else return cryptoAmount;
    }
}
