package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.DealType;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentTypeRepository extends BaseRepository<PaymentType> {

    /** SELECT **/

    @Query("from PaymentType where pid=:pid")
    PaymentType getByPid(@Param("pid") Long pid);

    @Query("from PaymentType where dealType=:dealType")
    List<PaymentType> getByDealType(@Param("dealType") DealType dealType);


    /** UPDATE **/
    @Modifying
    @Query("update PaymentType set isOn=:isOn where pid=:pid")
    void updateIsOnByPid(@Param("isOn") Boolean isOn, @Param("pid") Long pid);

    @Modifying
    @Query("update PaymentType set minSum=:minSum where pid=:pid")
    void updateMinSumByPid(@Param("minSum") BigDecimal minSum, @Param("pid") Long pid);

}