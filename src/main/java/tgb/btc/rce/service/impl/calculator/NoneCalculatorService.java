package tgb.btc.rce.service.impl.calculator;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tgb.btc.rce.enums.Command;

import java.util.List;

public class NoneCalculatorService extends SimpleCalculatorService {

    @Override
    public void addKeyboard(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(keyboardBuildService.buildInline(List.of(keyboardBuildService.getInlineBackButton()), 1));
    }

    @Override
    public void setCommand(Long chatId) {
        modifyUserService.updateStepAndCommandByChatId(chatId, Command.NONE_CALCULATOR.name(), 1);
    }

}
