package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.library.bean.web.api.ApiDeal;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.constants.enums.web.ApiDealStatus;
import tgb.btc.library.repository.web.ApiDealRepository;
import tgb.btc.library.repository.web.ApiUserRepository;
import tgb.btc.library.util.web.JacksonUtil;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.ApiDealProcessService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.web.constant.ControllerMapping;
import tgb.btc.web.constant.enums.ApiStatusCode;
import tgb.btc.web.util.SuccessResponseUtil;
import tgb.btc.web.vo.SuccessResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping(ControllerMapping.API10)
public class ApiController {

    private ApiDealRepository apiDealRepository;

    private AdminService adminService;

    private KeyboardService keyboardService;

    private ApiUserRepository apiUserRepository;

    private ApiDealProcessService apiDealProcessService;

    @Autowired
    public void setApiDealProcessService(ApiDealProcessService apiDealProcessService) {
        this.apiDealProcessService = apiDealProcessService;
    }

    @Autowired
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
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
        return "apiDocumentation";
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
        ApiStatusCode apiStatusCode = hasAccess(token);
        if (Objects.nonNull(apiStatusCode)) return apiStatusCode.toJson();
        return apiDealProcessService.newDeal(token, dealType, amount, cryptoAmount, cryptoCurrency, requisite, fiatCurrency);
    }

    @PostMapping("/paid")
    @ResponseBody
    public ObjectNode paid(@RequestParam(required = false) String token, @RequestParam(required = false) Long id) {
        ApiStatusCode apiStatusCode = hasAccess(token);
        if (Objects.nonNull(apiStatusCode)) return apiStatusCode.toJson();
        if (Objects.isNull(id)) return ApiStatusCode.DEAL_ID_EXPECTED.toJson();
        if (apiDealRepository.countByPid(id) == 0) {
            return ApiStatusCode.DEAL_NOT_EXISTS.toJson();
        } else if (ApiDealStatus.PAID.equals(apiDealRepository.getApiDealStatusByPid(id))) {
            return ApiStatusCode.DEAL_ALREADY_PAID.toJson();
        } else {
            ApiDeal apiDeal = apiDealRepository.getByPid(id);
            LocalDateTime now = LocalDateTime.now();
            if (now.minusMinutes(CommonProperties.VARIABLE.getLong(VariableType.DEAL_ACTIVE_TIME.getKey(), 15L)).isAfter(apiDeal.getDateTime())) {
                return ApiStatusCode.PAYMENT_TIME_IS_UP.toJson();
            }
            apiDealRepository.updateApiDealStatusByPid(ApiDealStatus.PAID, id);
            adminService.notify("Поступила новая api сделка.", keyboardService.getShowApiDeal(id));
            return ApiStatusCode.STATUS_PAID_UPDATED.toJson();
        }
    }

    @PostMapping("/cancel")
    @ResponseBody
    public ObjectNode cancel(@RequestParam(required = false) String token, @RequestParam(required = false) Long id) {
        ApiStatusCode apiStatusCode = hasAccess(token);
        if (Objects.nonNull(apiStatusCode)) return apiStatusCode.toJson();
        if (Objects.isNull(id)) return ApiStatusCode.DEAL_ID_EXPECTED.toJson();
        if (apiDealRepository.countByPid(id) == 0) {
            return ApiStatusCode.DEAL_NOT_EXISTS.toJson();
        } else {
            ApiDealStatus status = apiDealRepository.getApiDealStatusByPid(id);
            if (ApiDealStatus.CREATED.equals(status) || ApiDealStatus.PAID.equals(status)) {
                apiDealRepository.updateApiDealStatusByPid(ApiDealStatus.CANCELED, id);
                return ApiStatusCode.DEAL_DELETED.toJson();
            } else return ApiStatusCode.DEAL_CONFIRMED.toJson();
        }
    }

    @GetMapping("/getStatus")
    @ResponseBody
    public ObjectNode getStatus(@RequestParam(required = false) String token, @RequestParam(required = false) Long id) {
        ApiStatusCode apiStatusCode = hasAccess(token);
        if (Objects.nonNull(apiStatusCode)) return apiStatusCode.toJson();
        if (Objects.isNull(id)) return ApiStatusCode.DEAL_ID_EXPECTED.toJson();
        if (apiDealRepository.countByPid(id) == 0) {
            return ApiStatusCode.DEAL_NOT_EXISTS.toJson();
        } else {
            return ApiStatusCode.DEAL_EXISTS.toJson()
                    .set("data", MainWebController.DEFAULT_MAPPER.createObjectNode()
                            .put("status", apiDealRepository.getApiDealStatusByPid(id).name()));
        }
    }

    @GetMapping("/getUrl")
    @ResponseBody
    public ObjectNode getUrl() {
        return JacksonUtil.getEmpty()
                .put("success", true)
                .put("data", BotStringConstants.MAIN_URL);
    }

    @GetMapping("/getFiat")
    @ResponseBody
    public String getFiat() {
        return CommonProperties.CONFIG.getString("bot.fiat.currencies");
    }

    @GetMapping("/statusCodes/new")
    @ResponseBody
    public SuccessResponse<?> statusCodesNew() {
        return SuccessResponseUtil.data(ApiStatusCode.NEW_DEAL_STATUSES);
    }

    @GetMapping("/statusCodes/paid")
    @ResponseBody
    public SuccessResponse<?> statusCodesPaid() {
        return SuccessResponseUtil.data(ApiStatusCode.PAID_STATUSES);
    }

    @GetMapping("/statusCodes/cancel")
    @ResponseBody
    public SuccessResponse<?> statusCodesCancel() {
        return SuccessResponseUtil.data(ApiStatusCode.CANCEL_STATUSES);
    }

    @GetMapping("/statusCodes/getStatuses")
    @ResponseBody
    public SuccessResponse<?> statusCodesGetStatuses() {
        return SuccessResponseUtil.data(ApiStatusCode.GET_STATUS_STATUSES);
    }

    @GetMapping("/getDealStatuses")
    @ResponseBody
    public SuccessResponse<?> getDealStatuses() {
        return SuccessResponseUtil.data(List.of(ApiDealStatus.values()));
    }

    private ApiStatusCode hasAccess(String token) {
        if (StringUtils.isEmpty(token) || apiUserRepository.countByToken(token) == 0) {
            return ApiStatusCode.EMPTY_TOKEN;
        }
        if (BooleanUtils.isTrue(apiUserRepository.isBanned(apiUserRepository.getPidByToken(token)))) {
            return ApiStatusCode.USER_BANNED;
        }
        return null;
    }
}
