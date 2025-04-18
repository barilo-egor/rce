package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.Merchant;

public interface IBotMerchantService {

    void sendRequestPaymentType(CallbackQuery callbackQuery);

    void sendRequestMethod(Long chatId, String data, Integer messageId);

    void saveBind(Long chatId, String data, Integer messageId);

    void sendMerchantsMenu(Long chatId, Integer messageId);

    void sendIsOn(Long chatId, Integer messageId);

    void sendOrder(Long chatId, Integer messageId);

    void sendMaxAmounts(Long chatId, Integer messageId);

    void sendBindingMerchants(Long chatId, Integer messageId);

    void sendAutoConfirmMenu(Long chatId, Integer messageId);

    void sendStatusesMerchants(Long chatId, Integer messageId);

    void sendMerchantStatuses(Long chatId, Integer messageId, Merchant merchant);
}
