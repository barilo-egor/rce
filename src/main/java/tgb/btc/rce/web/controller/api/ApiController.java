package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.enums.ApiDealStatus;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.ApiDealService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.controller.api.enums.StatusCode;

import java.math.BigDecimal;

@Controller
@RequestMapping("/api")
public class ApiController {

    private ApiDealRepository apiDealRepository;

    private AdminService adminService;

    private KeyboardService keyboardService;

    private ApiDealService apiDealService;

    @Autowired
    public void setApiDealService(ApiDealService apiDealService) {
        this.apiDealService = apiDealService;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setApiDealRepository(ApiDealRepository apiDealRepository) {
        this.apiDealRepository = apiDealRepository;
    }

    @GetMapping("/documentation")
    public String documentation() {
        return "index";
    }

    @PostMapping("/new")
    @ResponseBody
    public ObjectNode newDeal(@RequestParam(required = false) String token,
                               @RequestParam(required = false) DealType dealType,
                               @RequestParam(required = false) BigDecimal amount,
                               @RequestParam(required = false) BigDecimal cryptoAmount,
                               @RequestParam(required = false) CryptoCurrency cryptoCurrency,
                               @RequestParam(required = false) String requisite,
                               @RequestParam(required = false) FiatCurrency fiatCurrency) {
        return apiDealService.newDeal(token, dealType, amount, cryptoAmount, cryptoCurrency, requisite, fiatCurrency);
    }

    @PostMapping("/paid")
    @ResponseBody
    public ObjectNode paid(@RequestParam Long id) {
        if (apiDealRepository.countByPid(id) == 0) {
            return StatusCode.DEAL_NOT_EXISTS.toJson();
        } else if (ApiDealStatus.PAID.equals(apiDealRepository.getApiDealStatusByPid(id))) {
            return StatusCode.DEAL_ALREADY_PAID.toJson();
        } else {
            apiDealRepository.updateApiDealStatusByPid(ApiDealStatus.PAID, id);
            adminService.notify("Поступила новая api сделка.", keyboardService.getShowApiDeal(id));
            return StatusCode.STATUS_PAID_UPDATED.toJson();
        }
    }

    @PostMapping("/cancel")
    @ResponseBody
    public ObjectNode cancel(@RequestParam Long id) {
        if (apiDealRepository.countByPid(id) == 0) {
            return StatusCode.DEAL_NOT_EXISTS.toJson();
        } else {
            ApiDealStatus status = apiDealRepository.getApiDealStatusByPid(id);
            if (ApiDealStatus.CREATED.equals(status) || ApiDealStatus.PAID.equals(status)) {
                apiDealRepository.deleteById(id);
                return StatusCode.DEAL_DELETED.toJson();
            } else return StatusCode.DEAL_CONFIRMED.toJson();
        }
    }

    @GetMapping("/getStatus")
    @ResponseBody
    public ObjectNode getStatus(@RequestParam Long id) {
        if (apiDealRepository.countByPid(id) == 0) {
            return StatusCode.DEAL_NOT_EXISTS.toJson();
        } else {
            return StatusCode.DEAL_EXISTS.toJson()
                    .set("data", MainWebController.DEFAULT_MAPPER.createObjectNode()
                            .put("status", apiDealRepository.getApiDealStatusByPid(id).name()));
        }
    }
}
