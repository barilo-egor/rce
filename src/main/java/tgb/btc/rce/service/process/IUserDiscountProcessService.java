package tgb.btc.rce.service.process;

import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.Rank;

import java.math.BigDecimal;

public interface IUserDiscountProcessService {

    BigDecimal applyDealDiscounts(Long chatId, BigDecimal dealAmount, Boolean isUsedPromo,
                                  Boolean isUserReferralDiscount, BigDecimal discount, FiatCurrency fiatCurrency);

    BigDecimal applyRank(Rank rank, Deal deal);
}
