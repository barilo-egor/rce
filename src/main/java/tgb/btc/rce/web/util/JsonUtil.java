package tgb.btc.rce.web.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import tgb.btc.library.interfaces.JsonConvertable;
import tgb.btc.rce.web.controller.MainWebController;

import java.util.List;
import java.util.stream.Collectors;

public class JsonUtil {

    public static <T extends JsonConvertable> ArrayNode toJsonArray(List<T> list) {
        return MainWebController.DEFAULT_MAPPER.createArrayNode()
                .addAll(list.stream()
                        .map(JsonConvertable::toJson)
                        .collect(Collectors.toList()));
    }
}
