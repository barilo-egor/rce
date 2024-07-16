package tgb.btc.rce.service.processors.tool;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.library.interfaces.service.bean.bot.IBotMessageService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.BACK)
public class Back extends Processor {

    private IBotMessageService botMessageService;

    @Autowired
    public void setBotMessageService(IBotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        responseSender.sendBotMessage(botMessageService.findByTypeNullSafe(BotMessageType.START),
                chatId,
                menuService.build(Menu.MAIN, readUserService.getUserRoleByChatId(chatId)));
    }
}
