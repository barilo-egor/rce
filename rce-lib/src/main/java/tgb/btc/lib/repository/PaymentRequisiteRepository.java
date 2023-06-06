package tgb.btc.lib.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.lib.bean.PaymentRequisite;
import tgb.btc.lib.bean.PaymentType;

import java.util.List;

@Repository
@Transactional
public interface PaymentRequisiteRepository extends BaseRepository<PaymentRequisite> {

    @Query("from PaymentRequisite where paymentType=:paymentType")
    List<PaymentRequisite> getByPaymentType(PaymentType paymentType);

    @Query("from PaymentRequisite where paymentType.pid=:paymentTypePid")
    List<PaymentRequisite> getByPaymentTypePid(Long paymentTypePid);

    @Query("select pr.paymentType from PaymentRequisite pr where pr.pid=:pid")
    PaymentType getPaymentTypeByPid(Long pid);

    @Query("select requisite from PaymentRequisite where paymentType.pid=:paymentPid and requisiteOrder=:requisiteOrder")
    String getRequisiteByPaymentTypePidAndOrder(Long paymentPid, Integer requisiteOrder);

    @Query("select count(pid) from PaymentRequisite where paymentType.pid=:paymentTypePid")
    Integer countByPaymentTypePid(Long paymentTypePid);

    @Modifying
    @Query("update PaymentRequisite set requisite=:requisite where pid=:pid")
    void updateRequisiteByPid(String requisite, Long pid);

    @Modifying
    @Query("delete from PaymentRequisite where paymentType.pid=:paymentTypePid")
    void deleteByPaymentTypePid(Long paymentTypePid);
}
