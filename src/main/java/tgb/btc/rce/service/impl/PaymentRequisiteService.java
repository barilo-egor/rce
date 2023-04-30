package tgb.btc.rce.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.repository.PaymentRequisiteRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class PaymentRequisiteService {

    private PaymentRequisiteRepository paymentRequisiteRepository;

    private final Map<Long, Integer> PAYMENT_REQUISITE_ORDER = new HashMap<>();

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    public Integer getOrder(Long paymentTypePid) {
        synchronized (this) {
            Integer order = PAYMENT_REQUISITE_ORDER.get(paymentTypePid);
            if (Objects.isNull(order)) {
                order = 1;
                PAYMENT_REQUISITE_ORDER.put(paymentTypePid, order);
            }
            return order;
        }
    }

    public void updateOrder(Long paymentTypePid) {
        synchronized (this) {
            Integer order = PAYMENT_REQUISITE_ORDER.get(paymentTypePid);
            if (Objects.isNull(order)) {
                order = 1;
                PAYMENT_REQUISITE_ORDER.put(paymentTypePid, order);
            } else {
                Integer paymentTypeRequisitesSize = paymentRequisiteRepository.countByPaymentTypePid(paymentTypePid);
                if (Objects.isNull(paymentTypeRequisitesSize) || paymentTypeRequisitesSize.equals(order)) order = 1;
                else PAYMENT_REQUISITE_ORDER.put(paymentTypePid, order + 1);
            }
        }
    }
}
