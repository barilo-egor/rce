package tgb.btc.rce.web.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tgb.btc.rce.enums.FiatCurrency;

import java.util.List;

@Controller
@RequestMapping("/web/enum")
public class EnumValuesController {

    @GetMapping("/fiatCurrencies")
    @ResponseBody
    public List<FiatCurrency> fiatCurrencies() {
        return List.of(FiatCurrency.values());
    }
}
