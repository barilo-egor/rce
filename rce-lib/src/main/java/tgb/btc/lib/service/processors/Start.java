package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.BotMessageType;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.BotMessageService;
import tgb.btc.lib.util.UpdateUtil;

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
        userService.deleteCurrentDeal(chatId);
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.START), chatId);
        processToMainMenu(chatId);
    }
}
