package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tgb.btc.rce.bean.ApiDeal;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.repository.ApiDealRepository;
import tgb.btc.rce.repository.ApiUserRepository;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.ApiDealService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.controller.api.enums.StatusCode;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.util.SuccessResponseUtil;
import tgb.btc.rce.web.vo.SuccessResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/api/10/")
public class ApiController {

    private ApiDealRepository apiDealRepository;

    private AdminService adminService;

    private KeyboardService keyboardService;

    private ApiDealService apiDealService;

    private ApiUserRepository apiUserRepository;

    @Autowired
    public void setApiUserRepository(ApiUserRepository apiUserRepository) {
        this.apiUserRepository = apiUserRepository;
    }

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
        StatusCode statusCode = hasAccess(token);
        if (Objects.nonNull(statusCode)) return statusCode.toJson();
        return apiDealService.newDeal(token, dealType, amount, cryptoAmount, cryptoCurrency, requisite, fiatCurrency);
    }

    @PostMapping("/paid")
    @ResponseBody
    public ObjectNode paid(@RequestParam(required = false) String token, @RequestParam(required = false) Long id) {
        StatusCode statusCode = hasAccess(token);
        if (Objects.nonNull(statusCode)) return statusCode.toJson();
        if (Objects.isNull(id)) return StatusCode.DEAL_ID_EXPECTED.toJson();
        if (apiDealRepository.countByPid(id) == 0) {
            return StatusCode.DEAL_NOT_EXISTS.toJson();
        } else if (ApiDealStatus.PAID.equals(apiDealRepository.getApiDealStatusByPid(id))) {
            return StatusCode.DEAL_ALREADY_PAID.toJson();
        } else {
            ApiDeal apiDeal = apiDealRepository.getByPid(id);
            LocalDateTime now = LocalDateTime.now();
            if (now.minusMinutes(BotProperties.BOT_VARIABLE.getLong(BotVariableType.DEAL_ACTIVE_TIME.getKey(), 15L)).isAfter(apiDeal.getDateTime())) {
                return StatusCode.PAYMENT_TIME_IS_UP.toJson();
            }
            apiDealRepository.updateApiDealStatusByPid(ApiDealStatus.PAID, id);
            adminService.notify("Поступила новая api сделка.", keyboardService.getShowApiDeal(id));
            return StatusCode.STATUS_PAID_UPDATED.toJson();
        }
    }

    @PostMapping("/cancel")
    @ResponseBody
    public ObjectNode cancel(@RequestParam(required = false) String token, @RequestParam(required = false) Long id) {
        StatusCode statusCode = hasAccess(token);
        if (Objects.nonNull(statusCode)) return statusCode.toJson();
        if (Objects.isNull(id)) return StatusCode.DEAL_ID_EXPECTED.toJson();
        if (apiDealRepository.countByPid(id) == 0) {
            return StatusCode.DEAL_NOT_EXISTS.toJson();
        } else {
            ApiDealStatus status = apiDealRepository.getApiDealStatusByPid(id);
            if (ApiDealStatus.CREATED.equals(status) || ApiDealStatus.PAID.equals(status)) {
                apiDealRepository.updateApiDealStatusByPid(ApiDealStatus.CANCELED, id);
                return StatusCode.DEAL_DELETED.toJson();
            } else return StatusCode.DEAL_CONFIRMED.toJson();
        }
    }

    @GetMapping("/getStatus")
    @ResponseBody
    public ObjectNode getStatus(@RequestParam(required = false) String token, @RequestParam(required = false) Long id) {
        StatusCode statusCode = hasAccess(token);
        if (Objects.nonNull(statusCode)) return statusCode.toJson();
        if (Objects.isNull(id)) return StatusCode.DEAL_ID_EXPECTED.toJson();
        if (apiDealRepository.countByPid(id) == 0) {
            return StatusCode.DEAL_NOT_EXISTS.toJson();
        } else {
            return StatusCode.DEAL_EXISTS.toJson()
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
        return BotProperties.BOT_CONFIG.getString("bot.fiat.currencies");
    }

    @GetMapping("/statusCodes/new")
    @ResponseBody
    public SuccessResponse<?> statusCodesNew() {
        return SuccessResponseUtil.data(StatusCode.NEW_DEAL_STATUSES);
    }

    @GetMapping("/statusCodes/paid")
    @ResponseBody
    public SuccessResponse<?> statusCodesPaid() {
        return SuccessResponseUtil.data(StatusCode.PAID_STATUSES);
    }

    @GetMapping("/statusCodes/cancel")
    @ResponseBody
    public SuccessResponse<?> statusCodesCancel() {
        return SuccessResponseUtil.data(StatusCode.CANCEL_STATUSES);
    }

    @GetMapping("/statusCodes/getStatuses")
    @ResponseBody
    public SuccessResponse<?> statusCodesGetStatuses() {
        return SuccessResponseUtil.data(StatusCode.GET_STATUS_STATUSES);
    }

    @GetMapping("/getDealStatuses")
    @ResponseBody
    public SuccessResponse<?> getDealStatuses() {
        return SuccessResponseUtil.data(List.of(ApiDealStatus.values()));
    }

    private StatusCode hasAccess(String token) {
        if (StringUtils.isEmpty(token) || apiUserRepository.countByToken(token) == 0) {
            return StatusCode.EMPTY_TOKEN;
        }
        if (BooleanUtils.isTrue(apiUserRepository.isBanned(apiUserRepository.getPidByToken(token)))) {
            return StatusCode.USER_BANNED;
        }
        return null;
    }
}
