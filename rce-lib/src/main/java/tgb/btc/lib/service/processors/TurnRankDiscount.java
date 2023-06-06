package tgb.btc.lib.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.BotVariableType;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.BotVariablePropertiesUtil;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.TURN_RANK_DISCOUNT)
public class TurnRankDiscount extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        boolean isOn = BotVariablePropertiesUtil.getBoolean(BotVariableType.DEAL_RANK_DISCOUNT_ENABLE);
        String message = isOn ? "Ранговая скидка включена для всех. Выключить?" : "Ранговая скидка выключена для всех. Включить?";
        String text = isOn ? "Выключить" : "Включить";
        String data = Command.TURNING_RANK_DISCOUNT.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER + !isOn;
        responseSender.sendMessage(chatId, message, KeyboardUtil.buildInline(List.of(InlineButton.builder()
                                                                                             .text(text)
                                                                                             .data(data)
                                                                                             .build())));
    }

}
