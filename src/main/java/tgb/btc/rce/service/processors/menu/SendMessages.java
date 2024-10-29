package tgb.btc.rce.service.processors.menu;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.SEND_MESSAGES)
public class SendMessages extends Processor {
    @Override
    public void run(Update update) {
        responseSender.sendMessage(updateService.getChatId(update), PropertiesMessage.SEND_MESSAGES_MENU, Menu.SEND_MESSAGES);
    }
}
