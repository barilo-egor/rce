package tgb.btc.rce.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.MenuFactory;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.BOT_SETTINGS)
public class BotSettings extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.BOT_SETTINGS_MENU),
                MenuFactory.build(Menu.BOT_SETTINGS, userService.isAdminByChatId(chatId)));
    }
}
