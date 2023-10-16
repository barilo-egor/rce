package tgb.btc.rce.enums;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.function.Function;

public enum DealStatus implements ObjectNodeConvertable<DealStatus> {
    NEW("Новая"),
    AWAITING_VERIFICATION("Ожидает верификацию"),
    VERIFICATION_REJECTED("Верификация отклонена"),
    VERIFICATION_RECEIVED("Верификация получена"),
    CONFIRMED("Подтверждена");

    private final String displayName;

    DealStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Function<DealStatus, ObjectNode> mapFunction() {
        return dealStatus -> JacksonUtil.getEmpty()
                .put("name", dealStatus.name())
                .put("displayName", dealStatus.getDisplayName());
    }
}
