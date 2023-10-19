package tgb.btc.rce.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.constants.enums.web.RoleConstants;
import tgb.btc.rce.web.constant.ControllerMapping;
import tgb.btc.rce.web.util.SuccessResponseUtil;
import tgb.btc.rce.web.vo.SuccessResponse;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping(ControllerMapping.ENUM)
public class EnumValuesController {

    @GetMapping("/fiatCurrencies")
    @ResponseBody
    public SuccessResponse<?> fiatCurrencies() {
        return SuccessResponseUtil.data(List.of(FiatCurrency.values()));
    }

    @GetMapping("/roles")
    @ResponseBody
    public SuccessResponse<?> roles() {
        return SuccessResponseUtil.data(List.of(RoleConstants.values()));
    }

    @GetMapping("/dealTypes")
    @ResponseBody
    public SuccessResponse<?> dealTypes() {
        return SuccessResponseUtil.data(Arrays.asList(DealType.values()));
    }
}
