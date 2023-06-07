package tgb.btc.lib.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.lib.bean.PaymentConfig;
import tgb.btc.lib.enums.PaymentTypeEnum;

@Repository
@Transactional
public interface PaymentConfigRepository extends BaseRepository<PaymentConfig> {

    @Query("from PaymentConfig where paymentTypeEnum=:paymentTypeEnum")
    PaymentConfig getByPaymentType(PaymentTypeEnum paymentTypeEnum);
}
