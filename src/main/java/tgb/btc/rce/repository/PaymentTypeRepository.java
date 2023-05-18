package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public interface PaymentTypeRepository extends BaseRepository<PaymentType> {

    /** SELECT **/

    @Query("from PaymentType where pid=:pid")
    PaymentType getByPid(@Param("pid") Long pid);

    @Query("from PaymentType where dealType=:dealType")
    List<PaymentType> getByDealType(@Param("dealType") DealType dealType);

    @Query("from PaymentType where dealType=:dealType and fiatCurrency=:fiatCurrency")
    List<PaymentType> getByDealTypeAndFiatCurrency(DealType dealType,  FiatCurrency fiatCurrency);

    @Query("from PaymentType where dealType=:dealType and isOn=:isOn and fiatCurrency=:fiatCurrency")
    List<PaymentType> getByDealTypeAndIsOnAndFiatCurrency(@Param("dealType") DealType dealType, @Param("isOn") Boolean isOn, FiatCurrency fiatCurrency);

    @Query("select count(pid) from PaymentType where dealType=:dealType and isOn=:isOn and fiatCurrency=:fiatCurrency")
    Integer countByDealTypeAndIsOnAndFiatCurrency(@Param("dealType") DealType dealType, @Param("isOn") Boolean isOn, FiatCurrency fiatCurrency);

    @Query("select dealType from PaymentType where pid=:pid")
    DealType getDealTypeByPid(@Param("pid") Long pid);


    /** UPDATE **/
    @Modifying
    @Query("update PaymentType set isOn=:isOn where pid=:pid")
    void updateIsOnByPid(@Param("isOn") Boolean isOn, @Param("pid") Long pid);

    @Modifying
    @Query("update PaymentType set minSum=:minSum where pid=:pid")
    void updateMinSumByPid(@Param("minSum") BigDecimal minSum, @Param("pid") Long pid);

    @Modifying
    @Query("update PaymentType set isDynamicOn=:isDynamicOn where pid=:pid")
    void updateIsDynamicOnByPid(@Param("isDynamicOn") Boolean isDynamicOn, @Param("pid") Long pid);
}
