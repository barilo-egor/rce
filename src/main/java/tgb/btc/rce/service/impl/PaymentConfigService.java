package tgb.btc.rce.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tgb.btc.rce.bean.PaymentConfig;
import tgb.btc.rce.enums.PaymentTypeEnum;
import tgb.btc.rce.repository.BaseRepository;
import tgb.btc.rce.repository.PaymentConfigRepository;

@Service
@Transactional
@Slf4j
public class PaymentConfigService extends BasePersistService<PaymentConfig> {

    private final PaymentConfigRepository paymentConfigRepository;

    @Autowired
    public PaymentConfigService(BaseRepository<PaymentConfig> baseRepository,
                                PaymentConfigRepository paymentConfigRepository) {
        super(baseRepository);
        this.paymentConfigRepository = paymentConfigRepository;
    }

    public PaymentConfig getByPaymentType(PaymentTypeEnum paymentTypeEnum) {
        try {
            PaymentConfig paymentConfig = paymentConfigRepository.getByPaymentType(paymentTypeEnum);
            if (paymentConfig == null)
                paymentConfig = paymentConfigRepository.save(PaymentConfig.builder()
                        .isOn(true)
                        .paymentTypeEnum(paymentTypeEnum)
                        .requisites("Отсутствуют")
                        .build());
            return paymentConfig;
        } catch (Exception e) {
            log.error("Ошибка при поиске payment config.", e);
            return null;
        }
    }
}
