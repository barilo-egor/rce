package tgb.btc.rce.bean;

import lombok.Builder;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PaymentTypeEnum;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "DEAL")
@Builder
public class Deal extends BasePersist {
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @Column(name = "DATE_TIME")
    private LocalDateTime dateTime;

    @Column(name = "DATE")
    private LocalDate date;

    @Column(name = "PAYMENT_TYPE")
    @Enumerated(value = EnumType.STRING)
    private PaymentTypeEnum paymentTypeEnum;

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

    @Column(name = "PAYMENT_RECEIPTS")
    @OneToMany
    private List<PaymentReceipt> paymentReceipts;

    @Column(name = "DISCOUNT")
    private BigDecimal discount;

    @Column(name = "ORIGINAL_PRICE")
    private BigDecimal originalPrice;

    public Deal() {
    }

    public Deal(User user, LocalDateTime dateTime, LocalDate date, PaymentTypeEnum paymentTypeEnum, BigDecimal cryptoAmount,
                BigDecimal amount, String wallet, String verificationPhoto, String userCheck, Boolean isActive,
                Boolean isPassed, Boolean isCurrent, Boolean isUsedPromo, Boolean isUsedReferralDiscount,
                CryptoCurrency cryptoCurrency, DealType dealType, BigDecimal commission,
                List<PaymentReceipt> paymentReceipts, BigDecimal discount, BigDecimal originalPrice) {
        this.user = user;
        this.dateTime = dateTime;
        this.date = date;
        this.paymentTypeEnum = paymentTypeEnum;
        this.cryptoAmount = cryptoAmount;
        this.amount = amount;
        this.wallet = wallet;
        this.verificationPhoto = verificationPhoto;
        this.userCheck = userCheck;
        this.isActive = isActive;
        this.isPassed = isPassed;
        this.isCurrent = isCurrent;
        this.isUsedPromo = isUsedPromo;
        this.isUsedReferralDiscount = isUsedReferralDiscount;
        this.cryptoCurrency = cryptoCurrency;
        this.dealType = dealType;
        this.commission = commission;
        this.paymentReceipts = paymentReceipts;
        this.discount = discount;
        this.originalPrice = originalPrice;
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

    public PaymentTypeEnum getPaymentTypeEnum() {
        return paymentTypeEnum;
    }

    public void setPaymentTypeEnum(PaymentTypeEnum paymentTypeEnum) {
        this.paymentTypeEnum = paymentTypeEnum;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Deal deal = (Deal) o;
        return Objects.equals(user, deal.user) && Objects.equals(dateTime, deal.dateTime) && Objects.equals(date, deal.date) && paymentTypeEnum == deal.paymentTypeEnum && Objects.equals(cryptoAmount, deal.cryptoAmount) && Objects.equals(amount, deal.amount) && Objects.equals(wallet, deal.wallet) && Objects.equals(verificationPhoto, deal.verificationPhoto) && Objects.equals(userCheck, deal.userCheck) && Objects.equals(isActive, deal.isActive) && Objects.equals(isPassed, deal.isPassed) && Objects.equals(isCurrent, deal.isCurrent) && Objects.equals(isUsedPromo, deal.isUsedPromo) && Objects.equals(isUsedReferralDiscount, deal.isUsedReferralDiscount) && cryptoCurrency == deal.cryptoCurrency && dealType == deal.dealType && Objects.equals(commission, deal.commission) && Objects.equals(paymentReceipts, deal.paymentReceipts) && Objects.equals(discount, deal.discount) && Objects.equals(originalPrice, deal.originalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user, dateTime, date, paymentTypeEnum, cryptoAmount, amount, wallet, verificationPhoto, userCheck, isActive, isPassed, isCurrent, isUsedPromo, isUsedReferralDiscount, cryptoCurrency, dealType, commission, paymentReceipts, discount, originalPrice);
    }

    @Override
    public String toString() {
        return "Deal{" +
                "user=" + user +
                ", dateTime=" + dateTime +
                ", date=" + date +
                ", paymentType=" + paymentTypeEnum +
                ", cryptoAmount=" + cryptoAmount +
                ", amount=" + amount +
                ", wallet='" + wallet + '\'' +
                ", verificationPhoto='" + verificationPhoto + '\'' +
                ", userCheck='" + userCheck + '\'' +
                ", isActive=" + isActive +
                ", isPassed=" + isPassed +
                ", isCurrent=" + isCurrent +
                ", isUsedPromo=" + isUsedPromo +
                ", isUsedReferralDiscount=" + isUsedReferralDiscount +
                ", cryptoCurrency=" + cryptoCurrency +
                ", dealType=" + dealType +
                ", commission=" + commission +
                ", paymentReceipts=" + paymentReceipts +
                '}';
    }
}