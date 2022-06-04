package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.PaymentType;

import java.math.BigDecimal;

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

    @Query("select count(d) from Deal d where d.user.chatId=:chatId")
    Long getDealsCountByUserChatId(@Param("chatId") Long chatId);

    Deal findByPid(Long pid);

    @Query("select amount from Deal where pid=:pid")
    BigDecimal getAmountByPid(@Param("pid") Long pid);

    @Modifying
    @Query("update Deal set wallet=:wallet where pid=:pid")
    void updateWalletByPid(@Param("wallet") String wallet, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set paymentType=:paymentType where pid=:pid")
    void updatePaymentTypeByPid(@Param("paymentType") PaymentType paymentType, @Param("pid") Long pid);

    @Modifying
    @Query("update Deal set isUsedPromo=:isUsedPromo where pid=:pid")
    void updateIsUsedPromoByPid(@Param("isUsedPromo") Boolean isUsedPromo, @Param("pid") Long pid);
}
