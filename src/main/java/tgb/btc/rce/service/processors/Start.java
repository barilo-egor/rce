package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.START)
public class Start extends Processor {

    private BotMessageService botMessageService;

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        run(chatId);
    }

    public void run(Long chatId) {
        userService.updateIsActiveByChatId(true, chatId);
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.START), chatId);
        processToMainMenu(chatId);
    }
}
