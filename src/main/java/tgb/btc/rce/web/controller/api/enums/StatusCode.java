package tgb.btc.rce.web.controller.api.enums;

import com.fasterxml.jackson.databind.node.ObjectNode;
import tgb.btc.rce.web.controller.MainWebController;
import tgb.btc.rce.web.interfaces.JsonConvertable;

public enum StatusCode implements JsonConvertable {
    CREATED_DEAL(0, "Сделка создана."),
    EMPTY_TOKEN(1, "Отсутствует токен."),
    EMPTY_DEAL_TYPE(2, "Отсутствует тип сделки."),
    EMPTY_AMOUNTS(3, "Не передана ни одна сумма(amount и cryptoAmount)."),
    ONLY_ONE_AMOUNT_NEEDED(4, "Должна быть заполнена только одна сумма(amount или cryptoAmount)."),
    EMPTY_CRYPTO_CURRENCY(5, "Отсутствует криптовалюта."),
    EMPTY_REQUISITE(6, "Отсутствует реквизит."),
    USER_NOT_FOUND(7, "Пользователь не найден."),

    DEAL_EXISTS(0, "Сделка найдена."),
    STATUS_PAID_UPDATED(0, "Сделка переведена в статус \"Оплачено\"."),
    DEAL_DELETED(0, "Сделка удалена."),
    DEAL_NOT_EXISTS(1, "Сделка не найдена."),
    DEAL_CONFIRMED(2, "Сделка уже обработана.");

    final int code;

    final String description;

    StatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public ObjectNode toJson() {
        return MainWebController.DEFAULT_MAPPER.createObjectNode()
                .put("code", this.code)
                .put("description", this.description);
    }
}
