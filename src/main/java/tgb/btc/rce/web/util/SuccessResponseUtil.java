package tgb.btc.rce.web.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.web.vo.SuccessResponse;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.List;
import java.util.function.Function;

public class SuccessResponseUtil {
    private SuccessResponseUtil() {
    }

    public static <T extends ObjectNodeConvertable<T>> SuccessResponse<?> data(T t) {
        return getDataObjectNode(JacksonUtil.toObjectNode(t));
    }

    public static <T extends ObjectNodeConvertable<T>> SuccessResponse<?> data(List<T> objects) {
        return getDataObjectNode(JacksonUtil.toArrayNode(objects));
    }

    public static <T> SuccessResponse<?> data(List<T> objects, Function<T, ObjectNode> mapper) {
        return getDataObjectNode(JacksonUtil.toArrayNode(objects, mapper));
    }

    public static <T> SuccessResponse<?> data(List<T> objects, ObjectNodeConvertable<T> mapper) {
        return getDataObjectNode(JacksonUtil.toArrayNode(objects, mapper));
    }

    public static <T> SuccessResponse<?> data(T t, Function<T, ObjectNode> mapper) {
        return getDataObjectNode(JacksonUtil.toObjectNode(t, mapper));
    }

    public static <T> SuccessResponse<?> data(T t, ObjectNodeConvertable<T> mapper) {
        return getDataObjectNode(JacksonUtil.toObjectNode(t, mapper));
    }

    public static <T> SuccessResponse<?> warningString(String warningString) {
        return new SuccessResponse<>(JacksonUtil.toObjectNode("warningString", warningString));
    }

    public static <T> SuccessResponse<?> message(String message) {
        return new SuccessResponse<>(JacksonUtil.toObjectNode("message", message));
    }

    /**
     * Возвращает сообщение исключения.
     */
    public static <T> SuccessResponse<?> exceptionString(Exception exception) {
        return new SuccessResponse<>(JacksonUtil.toObjectNode("exceptionString", exception.getMessage()));
    }

    public static SuccessResponse<?> getDataObjectNode(JsonNode jsonNode) {
        return getObjectNode(jsonNode, "data");
    }

    public static SuccessResponse<?> getObjectNode(JsonNode jsonNode, String fieldName) {
        return new SuccessResponse<>(JacksonUtil.DEFAULT_OBJECT_MAPPER.createObjectNode().set(fieldName,jsonNode));
    }
}
