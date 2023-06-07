package tgb.btc.lib.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.lib.bean.PaymentRequisite;
import tgb.btc.lib.bean.PaymentType;
import tgb.btc.lib.exception.BaseException;
import tgb.btc.lib.repository.PaymentRequisiteRepository;

import java.util.HashMap;
import java.util.List;
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

    private Integer getOrder(Long paymentTypePid) {
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
                if (Objects.isNull(paymentTypeRequisitesSize) || paymentTypeRequisitesSize.equals(order)) PAYMENT_REQUISITE_ORDER.put(paymentTypePid, 1);
                else PAYMENT_REQUISITE_ORDER.put(paymentTypePid, order + 1);
            }
        }
    }

    public String getRequisite(PaymentType paymentType) {
        String requisites;

        List<PaymentRequisite> paymentRequisite = paymentRequisiteRepository.getByPaymentTypePid(paymentType.getPid());
        if (CollectionUtils.isEmpty(paymentRequisite)) {
            throw new BaseException("Не установлены реквизиты для " + paymentType.getName() + ".");
        }
        if (BooleanUtils.isNotTrue(paymentType.getDynamicOn()) || paymentRequisite.size() == 1) {
            requisites = paymentRequisite.get(0).getRequisite();
        } else if (paymentRequisite.size() > 0){
            Integer order = getOrder(paymentType.getPid());
            requisites = paymentRequisiteRepository.getRequisiteByPaymentTypePidAndOrder(paymentType.getPid(), order);
        } else throw new BaseException("Не найдены реквизиты для " + paymentType.getName() + ".");
        return requisites;
    }
}
