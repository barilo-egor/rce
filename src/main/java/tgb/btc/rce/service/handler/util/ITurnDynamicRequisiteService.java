package tgb.btc.rce.service.handler.util;

import tgb.btc.library.constants.enums.bot.FiatCurrency;

public interface ITurnDynamicRequisiteService {
    void sendPaymentTypes(Long chatId, Integer messageId, FiatCurrency fiatCurrency);
}
