package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.TURNING_DELIVERY_TYPE)
@Slf4j
public class TurningDeliveryTypeProcessor extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, Command.TURNING_DELIVERY_TYPE.getText(), KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("вкл")
                        .data(CallbackQueryUtil.buildCallbackData(Command.TURN_PROCESS_DELIVERY.getText(), "true"))
                        .build(),
                InlineButton.builder()
                        .text("выкл")
                        .data(CallbackQueryUtil.buildCallbackData(Command.TURN_PROCESS_DELIVERY.getText(), "false"))
                        .build()
        )));
    }

}
