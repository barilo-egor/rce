package tgb.btc.rce.service;

import tgb.btc.library.constants.enums.Merchant;

public interface IBotMerchantService {
    void sendRequestPaymentType(Merchant merchant, Long chatId);

    void sendRequestMethod(Merchant merchant, Long chatId, String data, Integer messageId);

    void saveBind(Merchant merchant, Long chatId, String data, Integer messageId);
}
