package tgb.btc.lib.service.impl.calculator;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import tgb.btc.lib.conditional.NoneCalculatorCondition;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.util.KeyboardUtil;

import java.util.List;

@Service
@Conditional(NoneCalculatorCondition.class)
public class NoneCalculatorService extends SimpleCalculatorService {

    @Override
    public void addKeyboard(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(KeyboardUtil.buildInline(List.of(KeyboardUtil.INLINE_BACK_BUTTON), 1));
    }

    @Override
    public void setCommand(Long chatId) {
        userRepository.updateStepAndCommandByChatId(chatId, Command.NONE_CALCULATOR, 1);
    }

}
