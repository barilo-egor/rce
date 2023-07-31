package tgb.btc.rce.web.enums;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.bean.ApiDeal;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.function.Function;

public enum ApiDealMapper implements ObjectNodeConvertable<ApiDeal> {
    API_NEW(apiDeal -> JacksonUtil.getEmpty());

    Function<ApiDeal, ObjectNode> mapFunction;

    ApiDealMapper(Function<ApiDeal, ObjectNode> mapFunction) {
        this.mapFunction = mapFunction;
    }

    @Override
    public Function<ApiDeal, ObjectNode> mapFunction() {
        return null;
    }
}
