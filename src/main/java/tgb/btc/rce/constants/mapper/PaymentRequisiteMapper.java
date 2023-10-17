package tgb.btc.rce.constants.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.BooleanUtils;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.util.web.JacksonUtil;
import tgb.btc.library.interfaces.ObjectNodeConvertable;

import java.util.function.Function;

public enum PaymentRequisiteMapper implements ObjectNodeConvertable<PaymentRequisite> {
    GET_BY_PAYMENT_TYPE(paymentRequisite -> JacksonUtil.getEmpty()
            .put("pid", paymentRequisite.getPid())
            .put("name", paymentRequisite.getName())
            .put("requisite", paymentRequisite.getRequisite())
            .put("isOn", BooleanUtils.isTrue(paymentRequisite.getOn())));

    private final Function<PaymentRequisite, ObjectNode> mapFunction;

    PaymentRequisiteMapper(Function<PaymentRequisite, ObjectNode> mapFunction) {
        this.mapFunction = mapFunction;
    }

    @Override
    public Function<PaymentRequisite, ObjectNode> mapFunction() {
        return mapFunction;
    }
}
