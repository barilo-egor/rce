package tgb.btc.rce.service;

import tgb.btc.library.constants.enums.Merchant;

public interface IBotMerchantService {
    void sendRequestPaymentType(Merchant merchant, Long chatId);

    void sendRequestMethod(Long chatId, String data, Integer messageId);

    void saveBind(Long chatId, String data, Integer messageId);

    void sendMerchantsMenu(Long chatId, Integer messageId);

    void sendIsOn(Long chatId, Integer messageId);

    void sendOrder(Long chatId, Integer messageId);
}
