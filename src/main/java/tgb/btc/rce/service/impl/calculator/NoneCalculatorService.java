package tgb.btc.rce.service.impl.calculator;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tgb.btc.rce.conditional.calculator.NoneCalculatorCondition;
import tgb.btc.rce.enums.Command;

import java.util.List;

@Service
@Conditional(NoneCalculatorCondition.class)
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
