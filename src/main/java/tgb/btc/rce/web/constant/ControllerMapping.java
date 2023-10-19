package tgb.btc.rce.web.constant;

public interface ControllerMapping {

    String WEB = "/web";

    String API = "/api";
    String API10 = API + "/10";
    String API_USER = WEB + API + "/user";

    String REGISTRATION = WEB + "/registration";

    String DEAL = WEB + "/deal";
    String BOT_DEAL =  DEAL + "/bot";

    String PAYMENT_TYPES = WEB + "/paymentTypes";

    String PAYMENT = WEB + "/payment";
    String PAYMENT_REQUISITE = PAYMENT + "/requisite";

    String SETTINGS = WEB + "/settings";

    String BULK_DISCOUNTS = WEB + "/bulk_discounts";

    String ENUM = WEB + "/enum";

    String ROLES = WEB + "/role";
}
