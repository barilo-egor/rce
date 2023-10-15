package tgb.btc.rce.enums;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.interfaces.JsonConvertable;

public enum RoleConstants implements JsonConvertable {
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
    public ObjectNode toJson() {
        return MainWebController.DEFAULT_MAPPER.createObjectNode()
                .put("name", this.name())
                .put("displayName", this.getDisplayName());
    }
}
