package tgb.btc.rce.service.impl.calculator;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

public class InlineQueryCalculatorService extends SimpleCalculatorService {

    @Override
    public void addKeyboard(SendMessage sendMessage) {
        Long currentDealPid = readUserService.getCurrentDealByChatId(Long.parseLong(sendMessage.getChatId()));
        FiatCurrency fiatCurrency = dealPropertyService.getFiatCurrencyByPid(currentDealPid);
        DealType dealType = dealPropertyService.getDealTypeByPid(currentDealPid);
        CryptoCurrency currency = dealPropertyService.getCryptoCurrencyByPid(currentDealPid);
        sendMessage.setReplyMarkup(keyboardBuildService.buildInline(List.of(
                InlineButton.builder()
                        .inlineType(InlineType.SWITCH_INLINE_QUERY_CURRENT_CHAT)
                        .text("Калькулятор")
                        .data(fiatCurrency.getCode() + "-" + dealType.getKey() + "-" + currency.getShortName() + " ")
                        .build(),
                keyboardBuildService.getInlineBackButton()), 1));
    }

    @Override
    public void setCommand(Long chatId) {
        modifyUserService.updateStepAndCommandByChatId(chatId, Command.INLINE_QUERY_CALCULATOR.name(), 1);
    }

}
