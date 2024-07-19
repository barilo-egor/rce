package tgb.btc.rce.service.processors.menu;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.BOT_SETTINGS)
public class BotSettings extends Processor {
    @Override
    public void run(Update update) {
        responseSender.sendMessage(updateService.getChatId(update), PropertiesMessage.BOT_SETTINGS_MENU, Menu.BOT_SETTINGS);
    }
}
