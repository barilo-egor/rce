package tgb.btc.rce.enums;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.web.util.JacksonUtil;
import tgb.btc.rce.web.vo.interfaces.ObjectNodeConvertable;

import java.util.function.Function;

public enum RoleConstants implements ObjectNodeConvertable<RoleConstants> {
    ROLE_USER("Пользователь"),
    ROLE_OPERATOR("Оператор"),
    ROLE_ADMIN("Администратор");

    final String displayName;

    RoleConstants(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Function<RoleConstants, ObjectNode> mapFunction() {
        return roleConstants -> JacksonUtil.getEmpty()
                .put("name", roleConstants.name())
                .put("displayName", roleConstants.getDisplayName());
    }
}
