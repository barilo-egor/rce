package tgb.btc.rce.service.impl.calculator;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.conditional.calculator.InlineQueryCalculatorCondition;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
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
                        .data(fiatCurrency.getCode() + "-" + dealType.getKey() + "-" + currency.getShortName() + " ")
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON), 1));
    }

    @Override
    public void setCommand(Long chatId) {
        userRepository.updateStepAndCommandByChatId(chatId, Command.INLINE_QUERY_CALCULATOR.name(), 1);
    }

}
