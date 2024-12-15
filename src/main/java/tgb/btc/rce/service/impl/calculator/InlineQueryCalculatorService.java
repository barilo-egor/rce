package tgb.btc.rce.service.impl.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

public class InlineQueryCalculatorService extends SimpleCalculatorService {

    private IRedisUserStateService redisUserStateService;

    @Autowired
    public void setRedisUserStateService(IRedisUserStateService redisUserStateService) {
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public ReplyKeyboard getKeyboard(Long chatId) {
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        FiatCurrency fiatCurrency = dealPropertyService.getFiatCurrencyByPid(currentDealPid);
        DealType dealType = dealPropertyService.getDealTypeByPid(currentDealPid);
        CryptoCurrency currency = dealPropertyService.getCryptoCurrencyByPid(currentDealPid);
        return keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                        .text("Калькулятор")
                        .data(fiatCurrency.getCode() + "-" + dealType.getKey() + "-" + currency.getShortName() + " ")
                        .build(),
                keyboardBuildService.getInlineBackButton()), 1);
    }

    @Override
    public void setState(Long chatId) {
        redisUserStateService.save(chatId, UserState.INLINE_QUERY_CALCULATOR);
    }

}
