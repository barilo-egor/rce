package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.PaymentRequisite;
import tgb.btc.rce.bean.PaymentType;

import java.util.List;

@Repository
public interface PaymentRequisiteRepository extends BaseRepository<PaymentRequisite> {

    @Query("from PaymentRequisite where paymentType=:paymentType")
    List<PaymentRequisite> getByPaymentType(@Param("paymentType") PaymentType paymentType);

    @Modifying
    @Query("update PaymentRequisite set requisite=:requisite where pid=:pid")
    void updateRequisiteByPid(@Param("requisite") String requisite, @Param("pid") Long pid);
}
