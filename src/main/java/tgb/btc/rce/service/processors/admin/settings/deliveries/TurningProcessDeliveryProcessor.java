package tgb.btc.rce.service.processors.admin.settings.deliveries;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.service.module.DeliveryKindModule;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.Processor;

import java.util.List;

@CommandProcessor(command = Command.TURN_PROCESS_DELIVERY)
@Slf4j
public class TurningProcessDeliveryProcessor extends Processor {

    private IKeyboardService keyboardService;

    private DeliveryKindModule deliveryKindModule;

    @Autowired
    public void setDeliveryKindModule(DeliveryKindModule deliveryKindModule) {
        this.deliveryKindModule = deliveryKindModule;
    }

    @Autowired
    public void setKeyboardService(IKeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        String deliveryKind = callbackQueryService.getSplitData(update, 1);
        deliveryKindModule.set(DeliveryKind.valueOf(deliveryKind));
        responseSender.sendEditedMessageText(chatId, update.getCallbackQuery().getMessage().getMessageId(),
                commandService.getText(Command.TURNING_DELIVERY_TYPE),
                keyboardBuildService.buildInline(List.of(keyboardService.getDeliveryTypeButton())));
    }

}
