package tgb.btc.rce.service.impl.bean;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.PaymentRequisite;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.PaymentRequisiteRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        List<PaymentRequisite> paymentRequisite = paymentRequisiteRepository.getByPaymentTypePid(paymentType.getPid());
        if (CollectionUtils.isEmpty(paymentRequisite)) {
            throw new BaseException("Не установлены реквизиты для " + paymentType.getName() + ".");
        }
        if (BooleanUtils.isNotTrue(paymentType.getDynamicOn())) {
            return paymentRequisite.stream()
                    .filter(requisite -> BooleanUtils.isTrue(requisite.getOn()))
                    .findFirst()
                    .orElseThrow(() -> new BaseException("Не найден ни один включенный реквизит"))
                    .getRequisite();
        }
        List<PaymentRequisite> turnedRequisites = paymentRequisite.stream()
                .filter(requisite -> BooleanUtils.isTrue(requisite.getOn()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(turnedRequisites))
            throw new BaseException("Не найден ни один включенный реквизит.");
        Integer order = getOrder(paymentType.getPid());
        return turnedRequisites.get(order).getRequisite();
    }
}
