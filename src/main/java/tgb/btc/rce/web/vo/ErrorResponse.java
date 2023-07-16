package tgb.btc.rce.web.vo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.controller.api.enums.StatusCode;
import tgb.btc.rce.web.interfaces.JsonConvertable;

@Data
public class ErrorResponse implements JsonConvertable {

    @Getter
    @Setter
    private int statusCode;

    @Getter
    @Setter
    private String description;

    public ErrorResponse(StatusCode statusCode) {
        this.statusCode = statusCode.getCode();
        this.description = statusCode.getDescription();
    }

    @Override
    public ObjectNode toJson() {
        return MainWebController.DEFAULT_MAPPER.createObjectNode()
                .put("statusCode", statusCode)
                .put("description", description);
    }
}
