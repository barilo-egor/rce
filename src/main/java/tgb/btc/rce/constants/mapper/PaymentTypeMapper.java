package tgb.btc.rce.constants.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.Objects;
import java.util.function.Function;

public enum PaymentTypeMapper implements ObjectNodeConvertable<PaymentType> {
    FIND_ALL(paymentType -> JacksonUtil.getEmpty()
            .put("name", paymentType.getName())
            .put("isOn", paymentType.getOn())
            .put("minSum", paymentType.getMinSum().doubleValue())
            .put("dealType", Objects.nonNull(paymentType.getDealType())
                    ? paymentType.getDealType().getNominative() : null)
            .put("isDynamicOn", paymentType.getDynamicOn())
            .put("fiatCurrency", paymentType.getFiatCurrency().getDisplayName()));

    PaymentTypeMapper(Function<PaymentType, ObjectNode> mapFunction) {
        this.mapFunction = mapFunction;
    }

    private final Function<PaymentType, ObjectNode> mapFunction;

    @Override
    public Function<PaymentType, ObjectNode> mapFunction() {
        return mapFunction;
    }
}
