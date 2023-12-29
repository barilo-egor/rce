package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.CommonProperties;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;

@CommandProcessor(command = Command.TURN_PROCESS_DELIVERY)
@Slf4j
public class TurningProcessDeliveryProcessor extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String deliveryType = CallbackQueryUtil.getSplitData(update, 1);
        CommonProperties.MODULES.setProperty("delivery.kind", deliveryType);
        responseSender.sendEditedMessageText(chatId, update.getCallbackQuery().getMessage().getMessageId(),
                Command.TURNING_DELIVERY_TYPE.getText(),
                KeyboardUtil.buildInline(List.of(KeyboardUtil.getDeliveryTypeButton())));
    }

}
