package tgb.btc.rce.web.controller.paymentTypes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.mapper.PaymentRequisiteMapper;
import tgb.btc.rce.constants.mapper.PaymentTypeMapper;
import tgb.btc.rce.repository.PaymentRequisiteRepository;
import tgb.btc.rce.service.impl.PaymentTypeProcessService;
import tgb.btc.rce.service.impl.bean.PaymentTypeService;
import tgb.btc.rce.web.util.SuccessResponseUtil;
import tgb.btc.rce.web.vo.PaymentTypeVO;
import tgb.btc.rce.web.vo.SuccessResponse;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/web/paymentTypes")
@Slf4j
public class PaymentTypesController {

    private PaymentTypeService paymentTypeService;

    private PaymentRequisiteRepository paymentRequisiteRepository;

    private PaymentTypeProcessService paymentTypeProcessService;

    @Autowired
    public void setPaymentTypeProcessService(PaymentTypeProcessService paymentTypeProcessService) {
        this.paymentTypeProcessService = paymentTypeProcessService;
    }

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    @Autowired
    public void setPaymentTypeService(PaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @GetMapping("/findAll")
    public SuccessResponse<?> findAll() {
        return SuccessResponseUtil.data(paymentTypeService.findAll(), PaymentTypeMapper.FIND_ALL);
    }

    @PostMapping("/save")
    public SuccessResponse<?> save(@RequestBody PaymentTypeVO paymentTypeVO) {
        PaymentType paymentType;
        try {
            paymentType = paymentTypeProcessService.save(paymentTypeVO);
        } catch (EntityNotFoundException e) {
            return SuccessResponseUtil.warningString(e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при сохранении типа оплаты.", e);
            return SuccessResponseUtil.warningString(e.getMessage());
        }
        return SuccessResponseUtil.data(paymentType, PaymentTypeMapper.FIND_ALL);
    }

    @GetMapping("/get")
    public SuccessResponse<?> get(Long pid) {
        return SuccessResponseUtil.data(paymentTypeService.getByPid(pid), PaymentTypeMapper.GET);
    }

    @GetMapping("/getRequisites")
    public SuccessResponse<?> getRequisites(Long paymentTypePid) {
        return SuccessResponseUtil.data(paymentRequisiteRepository.getByPaymentTypePid(paymentTypePid),
                PaymentRequisiteMapper.GET_BY_PAYMENT_TYPE);
    }
}
