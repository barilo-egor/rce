package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.PaymentRequisite;
import tgb.btc.rce.bean.PaymentType;

import java.util.List;

@Repository
@Transactional
public interface PaymentRequisiteRepository extends BaseRepository<PaymentRequisite> {

    @Query("from PaymentRequisite where paymentType=:paymentType")
    List<PaymentRequisite> getByPaymentType(@Param("paymentType") PaymentType paymentType);

    @Query("from PaymentRequisite where paymentType.pid=:paymentTypePid")
    List<PaymentRequisite> getByPaymentTypePid(@Param("paymentTypePid") Long paymentTypePid);

    @Query("select pr.paymentType from PaymentRequisite pr where pr.pid=:pid")
    PaymentType getPaymentTypeByPid(@Param("pid") Long pid);

    @Query("select count(pid) from PaymentRequisite where paymentType.pid=:paymentTypePid")
    Long countByPaymentTypePid(@Param("paymentTypePid") Long paymentTypePid);

    @Modifying
    @Query("update PaymentRequisite set requisite=:requisite where pid=:pid")
    void updateRequisiteByPid(@Param("requisite") String requisite, @Param("pid") Long pid);
}
