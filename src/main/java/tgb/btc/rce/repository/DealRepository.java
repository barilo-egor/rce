package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PaymentTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public interface DealRepository extends BaseRepository<Deal> {

    @Modifying
    @Query("update Deal set cryptoCurrency=:cryptoCurrency where pid=:pid")
    void updateCryptoCurrencyByPid(@Param("pid") Long pid, @Param("cryptoCurrency") CryptoCurrency cryptoCurrency);

    @Query("select cryptoCurrency from Deal where pid=:pid")
    CryptoCurrency getCryptoCurrencyByPid(@Param("pid") Long pid);

    @Modifying
    @Query("update Deal set cryptoAmount=:cryptoAmount where pid=:pid")
    void updateCryptoAmountByPid(@Param("cryptoAmount") BigDecimal cryptoAmount, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set amount=:amount where pid=:pid")
    void updateAmountByPid(@Param("amount") BigDecimal amount, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set discount=:discount where pid=:pid")
    void updateDiscountByPid(@Param("discount") BigDecimal discount, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set commission=:commission where pid=:pid")
    void updateCommissionByPid(@Param("commission") BigDecimal commission, @Param("pid") Long pid);

    @Query("select commission from Deal where pid=:pid")
    BigDecimal getCommissionByPid(@Param("pid") Long pid);

    @Modifying
    @Query("update Deal set isUsedReferralDiscount=:isUsedReferralDiscount where pid=:pid")
    void updateUsedReferralDiscountByPid(@Param("isUsedReferralDiscount") Boolean isUsedReferralDiscount, @Param("pid") Long pid);

    @Query("select count(d) from Deal d where d.user.chatId=:chatId and d.isPassed=true and d.isActive=false")
    Long getPassedDealsCountByUserChatId(@Param("chatId") Long chatId);

    @Query("select count(d) from Deal d where d.user.chatId=:chatId and d.isPassed=false and d.dealType=:dealType")
    Long getPassedDealsCountByUserChatId(@Param("chatId") Long chatId, @Param("dealType") DealType dealType);

    Deal findByPid(Long pid);

    @Query("select amount from Deal where pid=:pid")
    BigDecimal getAmountByPid(@Param("pid") Long pid);

    @Query("select discount from Deal where pid=:pid")
    BigDecimal getDiscountByPid(@Param("pid") Long pid);

    @Modifying
    @Query("update Deal set wallet=:wallet where pid=:pid")
    void updateWalletByPid(@Param("wallet") String wallet, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set paymentTypeEnum=:paymentTypeEnum where pid=:pid")
    void updatePaymentTypeEnumByPid(@Param("paymentTypeEnum") PaymentTypeEnum paymentTypeEnum, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set paymentType=:paymentType where pid=:pid")
    void updatePaymentTypeByPid(@Param("paymentType") PaymentType paymentType, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set isUsedPromo=:isUsedPromo where pid=:pid")
    void updateIsUsedPromoByPid(@Param("isUsedPromo") Boolean isUsedPromo, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set isActive=:isActive where pid=:pid")
    void updateIsActiveByPid(@Param("isActive") Boolean isActive, @Param("pid") Long pid);

    @Query("select count(d) from Deal d where d.user.chatId=:chatId and d.isActive=true")
    Long getActiveDealsCountByUserChatId(@Param("chatId") Long chatId);

    @Query("select d.pid from Deal d where d.user.chatId=:chatId and d.isActive=true")
    Long getPidActiveDealByChatId(@Param("chatId") Long chatId);

    @Query("select count(d) from Deal d where d.isPassed=true and d.user.chatId=:chatId")
    Long getCountPassedByUserChatId(@Param("chatId") Long chatId);

    @Query("select pid from Deal where isActive=true")
    List<Long> getActiveDealPids();

    @Query("select d.user.chatId from Deal d where d.pid=:pid")
    Long getUserChatIdByDealPid(@Param("pid") Long pid);

    @Query("from Deal d where (d.date BETWEEN :startDate AND :endDate) and d.isPassed=true")
    List<Deal> getByDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("from Deal d where d.date=:date and d.isPassed=true")
    List<Deal> getPassedByDate(@Param("date") LocalDate dateTime);

    @Query("select wallet from Deal where pid=(select max(d.pid) from Deal d where d.isPassed=true and d.user.chatId=:chatId and d.dealType=:dealType)")
    String getWalletFromLastPassedByChatId(@Param("chatId") Long chatId, @Param("dealType") DealType dealType);

    @Query("select dealType from Deal where pid=:pid")
    DealType getDealTypeByPid(@Param("pid") Long pid);

    @Query(value = "select dateTime from Deal where pid=:pid")
    LocalDateTime getDateTimeByPid(Long pid);

    @Query(value = "update Deal set isPersonalApplied=:isPersonalApplied where pid=:pid")
    @Modifying
    void updateIsPersonalAppliedByPid(@Param("pid") Long pid, @Param("isPersonalApplied") Boolean isPersonalApplied);

    /**
     * Reports
     */

    @Query(value = "select sum(cryptoAmount) from Deal where isPassed=:isPassed and dealType=:dealType and date=:date and cryptoCurrency=:cryptoCurrency")
    BigDecimal getCryptoAmountSum(boolean isPassed, DealType dealType, LocalDate date, CryptoCurrency cryptoCurrency);

    @Query(value = "select sum(cryptoAmount) from Deal where isPassed=:isPassed and dealType=:dealType and (dateTime between :dateFrom and :dateTo) and cryptoCurrency=:cryptoCurrency")
    BigDecimal getCryptoAmountSum(boolean isPassed, DealType dealType, LocalDateTime dateFrom, LocalDateTime dateTo, CryptoCurrency cryptoCurrency);

    @Query(value = "select sum(amount) from Deal where isPassed=:isPassed and dealType=:dealType and dateTime=:dateTime and cryptoCurrency=:cryptoCurrency")
    BigDecimal getTotalAmountSum(boolean isPassed, DealType dealType, LocalDateTime dateTime, CryptoCurrency cryptoCurrency);

    @Query(value = "select sum(amount) from Deal where isPassed=:isPassed and dealType=:dealType and (dateTime between :dateFrom and :dateTo) and cryptoCurrency=:cryptoCurrency")
    BigDecimal getTotalAmountSum(boolean isPassed, DealType dealType, LocalDateTime dateFrom, LocalDateTime dateTo, CryptoCurrency cryptoCurrency);

    @Query(value = "select count(pid) from Deal where user.chatId=:chatId and isPassed=true")
    Integer getCountPassedByChatId(Long chatId);

    @Query(value = "select count(pid) from Deal where dateTime between :startDateTime and :endDateTime and isPassed=true")
    Integer getCountByPeriod(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query(value = "select sum(cryptoAmount) from Deal where user.chatId=:chatId and isPassed=true and cryptoCurrency=:cryptoCurrency and dealType=:dealType")
    BigDecimal getUserCryptoAmountSum(@Param("chatId") Long chatId, @Param("cryptoCurrency") CryptoCurrency cryptoCurrency,
                                      @Param("dealType") DealType dealType);

    @Query(value = "select sum(amount) from Deal where user.chatId=:chatId and isPassed=true and dealType=:dealType")
    BigDecimal getUserAmountSum(@Param("chatId") Long chatId, @Param("dealType") DealType dealType);
}
