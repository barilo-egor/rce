package tgb.btc.rce.constants.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.BooleanUtils;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.util.web.JacksonUtil;
import tgb.btc.library.interfaces.ObjectNodeConvertable;

import java.util.function.Function;

public enum PaymentTypeMapper implements ObjectNodeConvertable<PaymentType> {
    FIND_ALL(paymentType -> JacksonUtil.getEmpty()
            .put("pid", paymentType.getPid())
            .put("name", paymentType.getName())
            .put("isOn", paymentType.getOn())),
    GET(paymentType -> JacksonUtil.getEmpty()
            .put("pid", paymentType.getPid())
            .put("name", paymentType.getName())
            .put("isOn", BooleanUtils.isTrue(paymentType.getOn()))
            .put("fiatCurrency", paymentType.getFiatCurrency().name())
            .put("dealType", paymentType.getDealType().name())
            .put("minSum", paymentType.getMinSum())
            .put("isDynamicOn", BooleanUtils.isTrue(paymentType.getDynamicOn())));

    PaymentTypeMapper(Function<PaymentType, ObjectNode> mapFunction) {
        this.mapFunction = mapFunction;
    }

    private final Function<PaymentType, ObjectNode> mapFunction;

    @Override
    public Function<PaymentType, ObjectNode> mapFunction() {
        return mapFunction;
    }
}
