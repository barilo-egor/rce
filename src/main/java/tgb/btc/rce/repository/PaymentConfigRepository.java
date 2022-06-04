package tgb.btc.rce.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tgb.btc.rce.bean.PaymentConfig;
import tgb.btc.rce.enums.PaymentType;

@Repository
public interface PaymentConfigRepository extends BaseRepository<PaymentConfig> {

    @Query("from PaymentConfig where paymentType=:paymentType")
    PaymentConfig getByPaymentType(@Param("paymentType") PaymentType paymentType);
}
