package tgb.btc.rce.service.processors.admin.settings.deliveries;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.Processor;

import java.util.List;

@CommandProcessor(command = Command.TURNING_DELIVERY_TYPE)
@Slf4j
public class TurningDeliveryTypeProcessor extends Processor {

    private IKeyboardService keyboardService;

    @Autowired
    public void setKeyboardService(IKeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        responseSender.sendMessage(chatId, commandService.getText(Command.TURNING_DELIVERY_TYPE), keyboardBuildService.buildInline(List.of(keyboardService.getDeliveryTypeButton())));
    }

}
