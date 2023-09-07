package tgb.btc.rce.enums;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.function.Function;

public enum ApiDealStatus implements ObjectNodeConvertable<ApiDealStatus> {
    CREATED("Создана"),
    PAID("Оплачена"),
    CANCELED("Отменена клиентом"),
    ACCEPTED("Подтверждена оператором"),
    DECLINED("Отклонена оператором");

    private final String description;

    ApiDealStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Function<ApiDealStatus, ObjectNode> mapFunction() {
        return apiDealStatus -> JacksonUtil.getEmpty()
                .put("name", this.name())
                .put("description", this.getDescription());
    }
}
