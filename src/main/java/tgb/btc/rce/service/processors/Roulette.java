package tgb.btc.rce.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotMessageType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.BotMessageService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.ROULETTE)
public class Roulette extends Processor {

    private BotMessageService botMessageService;

    @Override
    public void run(Update update) {
        responseSender.sendBotMessage(botMessageService.findByType(BotMessageType.ROULETTE),
                UpdateUtil.getChatId(update));
    }
}
