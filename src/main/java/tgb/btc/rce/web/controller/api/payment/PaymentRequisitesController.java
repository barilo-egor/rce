package tgb.btc.rce.web.controller.api.payment;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.PaymentRequisiteRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.web.controller.MainWebController;

import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web/api/payment/requisite")
public class PaymentRequisitesController {

    private PaymentTypeRepository paymentTypeRepository;

    private PaymentRequisiteRepository paymentRequisiteRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    @GetMapping("/tree")
    @ResponseBody
    public ObjectNode tree(@RequestParam DealType dealType) {
        return MainWebController.DEFAULT_MAPPER.createObjectNode()
                .set("children", MainWebController.DEFAULT_MAPPER.createArrayNode()
                        .addAll(Arrays.stream(FiatCurrency.values())
                                .filter(fiatCurrency -> paymentTypeRepository.getCountByDealTypeAndFiatCurrency(dealType, fiatCurrency) > 0)
                                .map(fiatCurrency -> MainWebController.DEFAULT_MAPPER.createObjectNode()
                                        .put("text", fiatCurrency.name())
                                        .put("expanded", true)
                                        .put("iconCls", "fas fa-money-bill-wave customIconAlign")
                                        .<ObjectNode>set("children", getPaymentTypes(dealType, fiatCurrency)))
                                .collect(Collectors.toList())));
    }

    private ArrayNode getPaymentTypes(DealType dealType, FiatCurrency fiatCurrency) {
        return MainWebController.DEFAULT_MAPPER.createArrayNode().addAll(
                paymentTypeRepository.getByDealTypeAndFiatCurrency(dealType, fiatCurrency).stream()
                        .filter(paymentType -> paymentRequisiteRepository.getCountByPaymentType(paymentType) > 0)
                        .map(paymentType -> MainWebController.DEFAULT_MAPPER.createObjectNode()
                                .put("text", paymentType.getName())
                                .put("iconCls", "fas fa-money-check-alt customIconAlign")
                                .<ObjectNode>set("children", getRequisites(paymentType)))
                        .collect(Collectors.toList())
        );
    }

    private ArrayNode getRequisites(PaymentType paymentType) {
        return MainWebController.DEFAULT_MAPPER.createArrayNode().addAll(
                paymentRequisiteRepository.getByPaymentType(paymentType).stream()
                        .map(paymentRequisite -> MainWebController.DEFAULT_MAPPER.createObjectNode()
                                .put("text", paymentRequisite.getRequisite())
                                .put("iconCls", "far fa-credit-card customIconAlign")
                                .put("leaf", true))
                        .collect(Collectors.toList())
        );
    }
}
