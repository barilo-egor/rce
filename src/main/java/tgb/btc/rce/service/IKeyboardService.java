package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;

public interface IKeyboardService {

    ReplyKeyboard getCurrencies(DealType dealType);

    ReplyKeyboard getFiatCurrencies();

    ReplyKeyboard getPaymentTypes(DealType dealType, FiatCurrency fiatCurrency);

    ReplyKeyboard getShowDeal(Long dealPid);

    ReplyKeyboard getShowApiDeal(Long pid);

    ReplyKeyboard getUseReferralDiscount(BigDecimal sumWithDiscount, BigDecimal dealAmount);

    ReplyKeyboard getPromoCode(BigDecimal sumWithDiscount, BigDecimal dealAmount);

    ReplyKeyboard getInlineCalculator(Long chaId);

    ReplyKeyboard getInlineCalculatorSwitcher();

    ReplyKeyboard getDeliveryTypes(FiatCurrency fiatCurrency, DealType dealType, CryptoCurrency cryptoCurrency);

    InlineButton getDeliveryTypeButton();

    ReplyKeyboard getRPSRates();

    ReplyKeyboard getRPSElements();

    ReplyKeyboard getCabinetButtons();

    ReplyKeyboard getReplyCancel();

    ReplyKeyboard getInlineCancel();

    ReplyKeyboard getBuyOrSell();

    ReplyKeyboard getOperator();

    ReplyKeyboardMarkup getFiatCurrenciesKeyboard();

    ReplyKeyboard getBuildDeal();

    ReplyKeyboard getCancelDeal();

    ReplyKeyboard getDealReports();
}
