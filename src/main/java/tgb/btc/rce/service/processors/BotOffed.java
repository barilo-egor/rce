package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.BOT_OFFED)
public class BotOffed extends Processor {

    private final BotMessageService botMessageService;

    @Autowired
    public BotOffed(IResponseSender responseSender, UserService userService, BotMessageService botMessageService) {
        super(responseSender, userService);
        this.botMessageService = botMessageService;
    }

    @Override
    public void run(Update update) {
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.BOT_OFF), UpdateUtil.getChatId(update));
    }
}