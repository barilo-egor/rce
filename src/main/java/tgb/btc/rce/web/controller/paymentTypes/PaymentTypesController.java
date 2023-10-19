package tgb.btc.rce.web.controller.paymentTypes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.repository.bot.PaymentRequisiteRepository;
import tgb.btc.library.service.bean.bot.PaymentTypeService;
import tgb.btc.rce.constants.mapper.PaymentRequisiteMapper;
import tgb.btc.rce.constants.mapper.PaymentTypeMapper;
import tgb.btc.rce.service.impl.PaymentTypeProcessService;
import tgb.btc.web.constant.ControllerMapping;
import tgb.btc.web.util.SuccessResponseUtil;
import tgb.btc.web.vo.SuccessResponse;
import tgb.btc.web.vo.form.PaymentTypeVO;

import javax.persistence.EntityNotFoundException;

@RestController
@RequestMapping(ControllerMapping.PAYMENT_TYPES)
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
        return SuccessResponseUtil.data(paymentRequisiteRepository.getByPaymentType_Pid(paymentTypePid),
                PaymentRequisiteMapper.GET_BY_PAYMENT_TYPE);
    }

    @PostMapping("delete")
    public SuccessResponse<?> delete(Long pid) {
        return null;
    }
}
