package tgb.btc.rce.web.vo;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.web.controller.api.enums.StatusCode;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.Objects;
import java.util.function.Function;

public class ApiResponse implements ObjectNodeConvertable<ApiResponse> {

    private final StatusCode statusCode;

    private ObjectNode data;

    public ApiResponse(StatusCode statusCode, ObjectNode data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public ApiResponse(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public Function<ApiResponse, ObjectNode> mapFunction() {
        return apiResponse -> {
            ObjectNode result = JacksonUtil.getEmpty()
                    .put("code", statusCode.getCode())
                    .put("description", statusCode.getDescription());
            if (Objects.nonNull(data)) result.set("data", data);
            return result;
        };
    }
}
