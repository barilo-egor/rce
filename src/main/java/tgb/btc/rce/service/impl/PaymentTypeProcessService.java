package tgb.btc.rce.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.bean.PaymentRequisite;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.exception.EntityUniqueFieldException;
import tgb.btc.rce.repository.PaymentRequisiteRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.web.vo.PaymentTypeVO;
import tgb.btc.rce.web.vo.RequisiteVO;

import java.util.List;
import java.util.Objects;

@Service
public class PaymentTypeProcessService {

    private PaymentTypeRepository paymentTypeRepository;

    private PaymentRequisiteRepository paymentRequisiteRepository;

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    public PaymentType save(PaymentTypeVO paymentTypeVO) {
        if (Objects.isNull(paymentTypeVO.getPid()) && paymentTypeRepository.getCountByName(paymentTypeVO.getName()) > 0)
            throw new EntityUniqueFieldException("Тип оплаты с таким именем уже существует.");
        PaymentType paymentType;
        if (Objects.nonNull(paymentTypeVO.getPid())) {
            paymentType = paymentTypeRepository.getByPid(paymentTypeVO.getPid());
        } else {
            paymentType = new PaymentType();
        }
        paymentType.setName(paymentTypeVO.getName());
        paymentType.setOn(paymentTypeVO.getIsOn());
        paymentType.setFiatCurrency(paymentTypeVO.getFiatCurrency());
        paymentType.setDealType(paymentTypeVO.getDealType());
        paymentType.setMinSum(paymentTypeVO.getMinSum());
        paymentType.setDynamicOn(paymentTypeVO.getIsDynamicOn());
        paymentType = paymentTypeRepository.save(paymentType);
        if (Objects.nonNull(paymentTypeVO.getPid())) {
            List<PaymentRequisite> existsRequisites = paymentRequisiteRepository.getByPaymentTypePid(paymentTypeVO.getPid());
            if (CollectionUtils.isNotEmpty(existsRequisites)) {
                for (PaymentRequisite requisite : existsRequisites) {
                    if (paymentTypeVO.getRequisites().stream().noneMatch(req -> req.getPid().equals(requisite.getPid()))) {
                        paymentRequisiteRepository.delete(requisite);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(paymentTypeVO.getRequisites())) {
            for (RequisiteVO requisite: paymentTypeVO.getRequisites()) {
                PaymentRequisite paymentRequisite;
                if (Objects.nonNull(requisite.getPid())) {
                    paymentRequisite = paymentRequisiteRepository.getById(requisite.getPid());
                } else {
                    paymentRequisite = new PaymentRequisite();
                }
                paymentRequisite.setName(requisite.getName());
                paymentRequisite.setRequisite(requisite.getRequisite());
                paymentRequisite.setOn(requisite.getIsOn());
                paymentRequisite.setPaymentType(paymentType);
                paymentRequisiteRepository.save(paymentRequisite);
            }
        }
        return paymentType;
    }
}
