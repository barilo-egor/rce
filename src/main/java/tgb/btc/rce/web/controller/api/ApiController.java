package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.bean.ApiDeal;
import tgb.btc.rce.bean.ApiUser;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.repository.ApiUserRepository;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.controller.api.enums.StatusCode;
import tgb.btc.rce.web.vo.ApiDealVO;

import java.math.BigDecimal;
import java.util.Objects;

@Controller
@RequestMapping("/api")
public class ApiController {

    private ApiUserRepository apiUserRepository;

    private ApiDealRepository apiDealRepository;

    private AdminService adminService;

    private CalculateService calculateService;

    private CryptoCurrencyService cryptoCurrencyService;

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
                              @RequestParam(required = false) String requisite,
                              @RequestParam(required = false) FiatCurrency fiatCurrency) {
        ApiDealVO apiDealVO = new ApiDealVO(token, dealType, amount, cryptoAmount, cryptoCurrency, requisite, fiatCurrency);
        StatusCode code = apiDealVO.verify();
        if (Objects.nonNull(code)) return code.toJson();

        if (apiUserRepository.countByToken(token) == 0) {
            return StatusCode.USER_NOT_FOUND.toJson();
        }

        ApiUser apiUser = apiUserRepository.getByToken(token);
        ApiDeal apiDeal = apiDealService.create(apiDealVO, apiUser);
        BigDecimal minSum = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.MIN_SUM, dealType, cryptoCurrency);
        if (apiDeal.getCryptoAmount().compareTo(minSum) < 0) {
            return StatusCode.MIN_SUM.toJson().set("data",
                    MainWebController.DEFAULT_MAPPER.createObjectNode()
                            .put("minSum", BigDecimalUtil.roundToPlainString(minSum, 8)));
        }
        ObjectNode result = StatusCode.CREATED_DEAL.toJson()
                .set("data", MainWebController.DEFAULT_MAPPER.createObjectNode()
                        .put("id", apiDeal.getPid())
                        .put("amountToPay", BigDecimalUtil.roundToPlainString(apiDeal.getAmountToPay(), 8))
                        .put("requisite", apiUser.getRequisite(apiDeal.getDealType()))
                );
        if (DealType.isBuy(apiDeal.getDealType())) {
            result.put("cryptoAmount", apiDeal.getCryptoAmount());
        } else {
            result.put("amount", apiDeal.getAmount());
        }
        return result;
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
