package tgb.btc.rce.web.controller.api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.enums.RoleConstants;
import tgb.btc.rce.web.util.JsonUtil;

import java.util.List;

@Controller
@RequestMapping("/web/enum")
public class EnumValuesController {

    @GetMapping("/fiatCurrencies")
    @ResponseBody
    public ArrayNode fiatCurrencies() {
        return JsonUtil.toJsonArray(List.of(FiatCurrency.values()));
    }

    @GetMapping("/roles")
    @ResponseBody
    public ArrayNode roles() {
        return JsonUtil.toJsonArray(List.of(RoleConstants.values()));
    }
}
