package tgb.btc.rce.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.FiatCurrencyUtil;
import tgb.btc.rce.vo.web.CalculateDataForm;

@Controller
@RequestMapping("/settings")
public class SettingsController {

    @GetMapping(value = "/usdCourse")
    public String get() {
        return "settings/usdCourse";
    }

    @GetMapping(value = "/getUsdCourses")
    @ResponseBody
    public ObjectNode getCourses() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode fiatCurrencies = objectMapper.createArrayNode();
        for (FiatCurrency fiatCurrencyEnum : FiatCurrencyUtil.getFiatCurrencies()) {
            ObjectNode fiatCurrency = objectMapper.createObjectNode();
            fiatCurrency.put("displayName", fiatCurrencyEnum.name());
            fiatCurrency.put("name", fiatCurrencyEnum.name());
            ArrayNode dealTypes = objectMapper.createArrayNode();
            for (DealType dealTypeEnum : DealType.values()) {
                ObjectNode dealType = objectMapper.createObjectNode();
                dealType.put("displayName", dealTypeEnum.getNominativeFirstLetterToUpper());
                dealType.put("name", dealTypeEnum.name());
                ArrayNode cryptoCurrencies = objectMapper.createArrayNode();
                for (CryptoCurrency cryptoCurrencyEnum : CryptoCurrency.values()) {
                    ObjectNode cryptoCurrency = objectMapper.createObjectNode();
                    cryptoCurrency.put("defaultCheckValue", cryptoCurrencyEnum.getDefaultCheckValue());
                    cryptoCurrency.put("displayName", cryptoCurrencyEnum.name());
                    cryptoCurrency.put("name", cryptoCurrencyEnum.name());
                    cryptoCurrency.put("value",
                            BotVariablePropertiesUtil.getBigDecimal(BotVariableType.USD_COURSE, fiatCurrencyEnum, dealTypeEnum, cryptoCurrencyEnum));
                    cryptoCurrencies.add(cryptoCurrency);
                }
                dealType.set("cryptoCurrencies", cryptoCurrencies);
                dealTypes.add(dealType);
            }
            fiatCurrency.set("dealTypes", dealTypes);
            fiatCurrencies.add(fiatCurrency);
        }
        ObjectNode result = objectMapper.createObjectNode();
        result.put("success", true);
        result.set("data", fiatCurrencies);
        return result;
    }

    @GetMapping(value = "/calculate")
    @ResponseBody
    public ObjectNode calculate(CalculateDataForm calculateDataForm) {
        return null;
    }
}
