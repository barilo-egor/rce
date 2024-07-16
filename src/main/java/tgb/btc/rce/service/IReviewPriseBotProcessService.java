package tgb.btc.rce.service;

import tgb.btc.api.library.IReviewPriseProcessService;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.vo.ReviewPrise;

import java.math.BigDecimal;

public interface IReviewPriseBotProcessService extends IReviewPriseProcessService {
    ReviewPrise getReviewPrise(BigDecimal sum, FiatCurrency fiatCurrency);
}
