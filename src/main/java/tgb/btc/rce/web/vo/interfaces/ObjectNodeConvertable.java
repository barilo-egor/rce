package tgb.btc.rce.web.vo.interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.function.Function;

public interface ObjectNodeConvertable<T> {

    Function<T, ObjectNode> mapFunction();
}
