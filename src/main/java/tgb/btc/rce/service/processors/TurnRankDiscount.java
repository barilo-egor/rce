package tgb.btc.rce.service.processors;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.TURN_RANK_DISCOUNT)
public class TurnRankDiscount extends Processor {

    @Autowired
    public TurnRankDiscount(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

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
