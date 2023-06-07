package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.processors.support.BotMessagesService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.BOT_MESSAGES)
public class BotMessages extends Processor {

    private BotMessagesService botMessagesService;

    @Autowired
    public void setBotMessagesService(BotMessagesService botMessagesService) {
        this.botMessagesService = botMessagesService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (checkForCancel(update)) return;
        switch (userService.getStepByChatId(chatId)) {
            case 0:
                botMessagesService.askForType(chatId, Command.BOT_MESSAGES);
                break;
            case 1:
                botMessagesService.askForNewValue(update);
                break;
            case 2:
                botMessagesService.updateValue(update);
                processToAdminMainPanel(chatId);
                break;
        }
    }
}
