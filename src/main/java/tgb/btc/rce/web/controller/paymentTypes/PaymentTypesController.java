package tgb.btc.rce.web.controller.paymentTypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tgb.btc.rce.constants.mapper.PaymentTypeMapper;
import tgb.btc.rce.service.impl.PaymentTypeService;
import tgb.btc.rce.web.util.SuccessResponseUtil;
import tgb.btc.rce.web.vo.SuccessResponse;

@Controller
@RequestMapping("/web/paymentTypes")
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
}
