package tgb.btc.rce.web.util;

import com.fasterxml.jackson.databind.node.ArrayNode;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.interfaces.JsonConvertable;

import java.util.List;
import java.util.stream.Collectors;

public class JsonUtil {

    public static ArrayNode toJsonArray(List<JsonConvertable> list) {
        return MainWebController.DEFAULT_MAPPER.createArrayNode()
                .addAll(list.stream()
                        .map(JsonConvertable::toJson)
                        .collect(Collectors.toList()));
    }
}
