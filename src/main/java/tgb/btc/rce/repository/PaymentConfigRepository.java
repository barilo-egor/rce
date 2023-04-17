package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.PaymentConfig;
import tgb.btc.rce.enums.PaymentTypeEnum;

@Repository
@Transactional
public interface PaymentConfigRepository extends BaseRepository<PaymentConfig> {

    @Query("from PaymentConfig where paymentType=:paymentType")
    PaymentConfig getByPaymentType(@Param("paymentType") PaymentTypeEnum paymentTypeEnum);
}
