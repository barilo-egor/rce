package tgb.btc.rce.enums;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.Arrays;
import java.util.function.Function;

public enum CryptoCurrency implements ObjectNodeConvertable<CryptoCurrency> {
    BITCOIN("btc", 8, 0.004),
    LITECOIN("ltc", 8, 0.7),
    USDT("usdt", 1, 50.0),
    MONERO("xmr", 8, 0.5);

    final String shortName;
    final int scale;
    final Double defaultCheckValue;

    CryptoCurrency(String shortName, int scale, Double defaultCheckValue) {
        this.shortName = shortName;
        this.scale = scale;
        this.defaultCheckValue = defaultCheckValue;
    }

    public Double getDefaultCheckValue() {
        return defaultCheckValue;
    }

    public int getScale() {
        return scale;
    }

    public String getShortName() {
        return shortName;
    }

    public static CryptoCurrency fromShortName(String shortName) {
        return Arrays.stream(CryptoCurrency.values()).filter(t -> t.getShortName().equals(shortName)).findFirst()
                .orElseThrow(() -> new EnumTypeNotFoundException("Не найдена крипто валюта: " + shortName));
    }
    @Override
    public Function<CryptoCurrency, ObjectNode> mapFunction() {
        return cryptoCurrency -> JacksonUtil.getEmpty()
                .put("name", cryptoCurrency.name());
    }
}
