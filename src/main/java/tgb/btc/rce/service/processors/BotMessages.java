package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.processors.support.BotMessagesService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.BOT_MESSAGES)
public class BotMessages extends Processor {

    private final BotMessagesService botMessagesService;

    @Autowired
    public BotMessages(IResponseSender responseSender, UserService userService, BotMessagesService botMessagesService) {
        super(responseSender, userService);
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
