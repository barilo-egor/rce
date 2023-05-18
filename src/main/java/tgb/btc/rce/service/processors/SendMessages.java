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

@CommandProcessor(command = Command.SEND_MESSAGES)
public class SendMessages extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.SEND_MESSAGES_MENU),
                MenuFactory.build(Menu.SEND_MESSAGES, userService.isAdminByChatId(chatId)));
    }
}
