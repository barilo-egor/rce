package tgb.btc.rce.web.controller.paymentTypes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.mapper.PaymentTypeMapper;
import tgb.btc.rce.service.impl.PaymentTypeService;
import tgb.btc.rce.web.util.SuccessResponseUtil;
import tgb.btc.rce.web.vo.PaymentTypeVO;
import tgb.btc.rce.web.vo.SuccessResponse;

import javax.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/web/paymentTypes")
@Slf4j
public class PaymentTypesController {

    private PaymentTypeService paymentTypeService;

    @Autowired
    public void setPaymentTypeService(PaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @GetMapping("/findAll")
    @ResponseBody
    public SuccessResponse<?> findAll() {
        return SuccessResponseUtil.data(paymentTypeService.findAll(), PaymentTypeMapper.FIND_ALL);
    }

    @PostMapping("/save")
    @ResponseBody
    public SuccessResponse<?> save(@RequestBody PaymentTypeVO paymentTypeVO) {
        PaymentType paymentType;
        try {
            paymentType = paymentTypeService.save(paymentTypeVO);
        } catch (EntityNotFoundException e) {
            return SuccessResponseUtil.warningString(e.getMessage());
        } catch (Exception e) {
            log.error("Ошибка при сохранении типа оплаты.", e);
            return SuccessResponseUtil.warningString(e.getMessage());
        }
        return SuccessResponseUtil.data(paymentType, PaymentTypeMapper.FIND_ALL);
    }
}
