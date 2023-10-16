package tgb.btc.rce.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import tgb.btc.rce.enums.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "DEAL")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Deal extends BasePersist {
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;

    @Deprecated
    @Column(name = "DATE")
    private LocalDate date;

    @Deprecated
    @Column(name = "PAYMENT_TYPE_ENUM")
    @Enumerated(value = EnumType.STRING)
    /**
     * use paymentType
     */
    private PaymentTypeEnum paymentTypeEnum;

    @ManyToOne
    private PaymentType paymentType;

    @Column(name = "CRYPTO_AMOUNT", precision = 15, scale = 8)
    private BigDecimal cryptoAmount;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "WALLET")
    private String wallet;

    @Column(name = "VERIFICATION_PHOTO")
    private String verificationPhoto;

    @Column(name = "USER_CHECK")
    @Deprecated
    private String userCheck;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    @Column(name = "IS_PASSED")
    private Boolean isPassed;

    @Column(name = "IS_CURRENT")
    private Boolean isCurrent;

    @Column(name = "IS_USED_PROMO")
    private Boolean isUsedPromo;

    @Column(name = "IS_USED_REFERRAL_DISCOUNT")
    private Boolean isUsedReferralDiscount;

    @Column(name = "CRYPTO_CURRENCY")
    @Enumerated(value = EnumType.STRING)
    private CryptoCurrency cryptoCurrency;

    @Column(name = "DEAL_TYPE")
    @Enumerated(value = EnumType.STRING)
    private DealType dealType;

    @Column(name = "COMMISSION")
    private BigDecimal commission;

    @OneToMany
    private List<PaymentReceipt> paymentReceipts;

    @Column(name = "DISCOUNT")
    private BigDecimal discount;

    @Column(name = "ORIGINAL_PRICE")
    private BigDecimal originalPrice;

    @Column(name = "BULK_APPLIED")
    private Boolean isBulkApplied;

    @Column(name = "IS_PERSONAL_APPLIED")
    private Boolean isPersonalApplied;

    @Column(name = "FIAT_CURRENCY")
    @Enumerated(value = EnumType.STRING)
    private FiatCurrency fiatCurrency;

    @Column(name = "DEAL_STATUS")
    @Enumerated(value = EnumType.STRING)
    private DealStatus dealStatus;

    public Boolean getCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean current) {
        isCurrent = current;
    }

    public FiatCurrency getFiatCurrency() {
        return fiatCurrency;
    }

    public void setFiatCurrency(FiatCurrency fiatCurrency) {
        this.fiatCurrency = fiatCurrency;
    }

    public String getUserCheck() {
        return userCheck;
    }

    public void setUserCheck(String check) {
        this.userCheck = check;
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

    @Deprecated
    /**
     * use paymentType
     */
    // TODO написать скрипт по пере
    public PaymentTypeEnum getPaymentTypeEnum() {
        return paymentTypeEnum;
    }

    @Deprecated
    /**
     * use paymentType
     */
    public void setPaymentTypeEnum(PaymentTypeEnum paymentTypeEnum) {
        this.paymentTypeEnum = paymentTypeEnum;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Boolean getUsedReferralDiscount() {
        return isUsedReferralDiscount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public void setUsedReferralDiscount(Boolean usedReferralDiscount) {
        isUsedReferralDiscount = usedReferralDiscount;
    }

    public List<PaymentReceipt> getPaymentReceipts() {
        return paymentReceipts;
    }

    public void setPaymentReceipts(List<PaymentReceipt> paymentReceipts) {
        this.paymentReceipts = paymentReceipts;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Boolean getBulkApplied() {
        return isBulkApplied;
    }

    public void setBulkApplied(Boolean bulkApplied) {
        isBulkApplied = bulkApplied;
    }

    public Boolean getPersonalApplied() {
        return isPersonalApplied;
    }

    public void setPersonalApplied(Boolean personalApplied) {
        isPersonalApplied = personalApplied;
    }

    public DealStatus getDealStatus() {
        return dealStatus;
    }

    public void setDealStatus(DealStatus dealStatus) {
        this.dealStatus = dealStatus;
    }
}