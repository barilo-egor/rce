package tgb.btc.rce.service.handler.util;

import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;

public interface IShowPaymentTypesService {
    void sendForTurn(Long chatId, DealType dealType, FiatCurrency fiatCurrency, Integer messageId);
}
