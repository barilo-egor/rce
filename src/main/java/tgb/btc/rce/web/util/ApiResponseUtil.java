package tgb.btc.rce.web.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.web.controller.api.enums.StatusCode;
import tgb.btc.rce.web.vo.ApiResponse;

public final class ApiResponseUtil {

    private ApiResponseUtil() {
    }

    public static ObjectNode build(StatusCode statusCode) {
        return build(statusCode, null);
    }

    public static ObjectNode build(StatusCode statusCode, ObjectNode data) {
        return JacksonUtil.toObjectNode(new ApiResponse(statusCode, data));
    }
}
