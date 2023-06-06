package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.BotMessageType;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.Menu;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.BotMessageService;
import tgb.btc.lib.util.MenuFactory;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.BACK)
public class Back extends Processor {
    private BotMessageService botMessageService;

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.START),
                chatId,
                MenuFactory.build(Menu.MAIN, userService.isAdminByChatId(chatId)));
    }
}
