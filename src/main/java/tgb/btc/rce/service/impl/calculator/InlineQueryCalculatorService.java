package tgb.btc.rce.service.impl.calculator;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tgb.btc.rce.conditional.InlineQueryCalculatorCondition;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
@Conditional(InlineQueryCalculatorCondition.class)
public class InlineQueryCalculatorService extends SimpleCalculatorService {

    @Override
    public void addKeyboard(SendMessage sendMessage) {
        Long currentDealPid = userRepository.getCurrentDealByChatId(Long.parseLong(sendMessage.getChatId()));
        FiatCurrency fiatCurrency = dealRepository.getFiatCurrencyByPid(currentDealPid);
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        CryptoCurrency currency = dealRepository.getCryptoCurrencyByPid(currentDealPid);
        sendMessage.setReplyMarkup(KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                        .text("Калькулятор")
                        .data(fiatCurrency.getCode() + "-" + dealType.getKey() + "-" + currency.getShortName())
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON), 1));
    }
}