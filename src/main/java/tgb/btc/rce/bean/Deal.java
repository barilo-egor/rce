package tgb.btc.rce.bean;

import lombok.Builder;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PaymentType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "DEAL")
@Builder
public class Deal extends BasePersist {
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;

    @Column(name = "PAYMENT_TYPE")
    @Enumerated(value = EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "BTC_AMOUNT", precision = 15, scale = 8)
    private BigDecimal btcAmount;

    @Column(name = "CRYPTO_AMOUNT", precision = 15, scale = 8)
    private BigDecimal cryptoAmount;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "WALLET")
    private String wallet;

    @Column(name = "VERIFICATION_PHOTO")
    private String verificationPhoto;

    @Column(name = "USER_CHECK")
    private String userCheck;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    @Column(name = "IS_PASSED")
    private Boolean isPassed;

    @Column(name = "IS_CURRENT")
    private Boolean isCurrent;

    @Column(name = "IS_USED_PROMO")
    private Boolean isUsedPromo;

    @Column(name = "CRYPTO_CURRENCY")
    @Enumerated(value = EnumType.STRING)
    private CryptoCurrency cryptoCurrency;

    @Column(name = "DEAL_TYPE")
    @Enumerated(value = EnumType.STRING)
    private DealType dealType;

    public Deal() {
    }

    public Deal(User user, LocalDateTime dateTime, PaymentType paymentType, BigDecimal btcAmount,
                BigDecimal cryptoAmount, BigDecimal amount, String wallet, String verificationPhoto, String userCheck,
                Boolean isActive, Boolean isPassed, Boolean isCurrent, Boolean isUsedPromo,
                CryptoCurrency cryptoCurrency, DealType dealType) {
        this.user = user;
        this.dateTime = dateTime;
        this.paymentType = paymentType;
        this.btcAmount = btcAmount;
        this.cryptoAmount = cryptoAmount;
        this.amount = amount;
        this.wallet = wallet;
        this.verificationPhoto = verificationPhoto;
        this.userCheck = userCheck;
        this.isActive = isActive;
        this.isPassed = isPassed;
        this.isCurrent = isCurrent;
        this.isUsedPromo = isUsedPromo;
        this.cryptoCurrency = cryptoCurrency;
        this.dealType = dealType;
    }

    public String getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(String check) {
        this.userCheck = check;
    }

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getPassed() {
        return isPassed;
    }

    public void setPassed(Boolean passed) {
        isPassed = passed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public BigDecimal getBtcAmount() {
        return btcAmount;
    }

    public void setBtcAmount(BigDecimal btcAmount) {
        this.btcAmount = btcAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getVerificationPhoto() {
        return verificationPhoto;
    }

    public void setVerificationPhoto(String verificationPhoto) {
        this.verificationPhoto = verificationPhoto;
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

    public Boolean getUsedPromo() {
        return isUsedPromo;
    }

    public void setUsedPromo(Boolean usedPromo) {
        isUsedPromo = usedPromo;
    }

    public DealType getDealType() {
        return dealType;
    }

    public void setDealType(DealType dealType) {
        this.dealType = dealType;
    }

    // TODO
}