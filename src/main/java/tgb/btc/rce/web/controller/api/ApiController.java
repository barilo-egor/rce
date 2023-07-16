package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.bean.ApiDeal;
import tgb.btc.rce.bean.ApiUser;
import tgb.btc.rce.enums.ApiDealStatus;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.repository.ApiUserRepository;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.CalculateService;
import tgb.btc.rce.service.impl.CryptoCurrencyService;
import tgb.btc.rce.util.BulkDiscountUtil;
import tgb.btc.rce.vo.web.CalculateDataForm;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.controller.api.enums.StatusCode;
import tgb.btc.rce.web.vo.ApiDealVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Controller
@RequestMapping("/api")
public class ApiController {

    private ApiUserRepository apiUserRepository;

    private ApiDealRepository apiDealRepository;

    private AdminService adminService;

    private CalculateService calculateService;

    private CryptoCurrencyService cryptoCurrencyService;

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setCryptoCurrencyService(CryptoCurrencyService cryptoCurrencyService) {
        this.cryptoCurrencyService = cryptoCurrencyService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setApiDealRepository(ApiDealRepository apiDealRepository) {
        this.apiDealRepository = apiDealRepository;
    }

    @Autowired
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
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
                              @RequestParam(required = false) String requisite) {
        ApiDealVO apiDealVO = new ApiDealVO(token, dealType, amount, cryptoAmount, cryptoCurrency, requisite);
        StatusCode code = apiDealVO.verify();
        if (Objects.nonNull(code)) return code.toJson();

        if (apiUserRepository.countByToken(token) == 0) {
            return StatusCode.USER_NOT_FOUND.toJson();
        }

        ApiUser apiUser = apiUserRepository.getByToken(token);
        ApiDeal apiDeal = new ApiDeal();
        apiDeal.setApiUser(apiUser);
        apiDeal.setDateTime(LocalDateTime.now());
        apiDeal.setDealType(dealType);
        if (Objects.nonNull(apiDealVO.getAmount())) {
            calculateService.calculate(CalculateDataForm.builder()
                            .amount(apiDealVO.getAmount())
                            .dealType(apiDealVO.getDealType())
                            .fiatCurrency(apiUser.getFiatCurrency())
                            .usdCourse(apiUser.getUsdCourse())
                            .cryptoCourse(cryptoCurrencyService.getCurrency(cryptoCurrency))
                            .personalDiscount(apiUser.getPersonalDiscount())
                            .cryptoCurrency(apiDealVO.getCryptoCurrency())
                            .bulkDiscount(BulkDiscountUtil.getPercentBySum(amount, apiUser.getFiatCurrency()))
                    .build());
            apiDeal.setAmount(apiDealVO.getAmount());
        } else {
            apiDeal.setCryptoAmount(apiDealVO.getCryptoAmount());
        }
        apiDeal.setApiDealStatus(ApiDealStatus.CREATED);
        apiDeal.setCryptoCurrency(apiDealVO.getCryptoCurrency());
        apiDeal.setRequisite(apiDealVO.getRequisite());
        apiDeal = apiDealRepository.save(apiDeal);
        adminService.notify("Поступила новая api сделка на " + apiDeal.getDealType().getGenitive() + ".");
        return StatusCode.CREATED_DEAL.toJson()
                .set("data", MainWebController.DEFAULT_MAPPER.createObjectNode()
                        .put("id", apiDeal.getPid())
                        .put("amount", apiDeal.getAmountToPay())
                        .put("requisite", apiUser.getRequisite(apiDeal.getDealType()))
                );
    }

    @PostMapping("/paid")
    @ResponseBody
    public ObjectNode paid(@RequestParam Long id) {
        if (apiDealRepository.countByPid(id) == 0) {
            return StatusCode.DEAL_NOT_EXISTS.toJson();
        } else {
            apiDealRepository.updateApiDealStatusByPid(ApiDealStatus.PAID, id);
            return StatusCode.DEAL_EXISTS.toJson();
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
