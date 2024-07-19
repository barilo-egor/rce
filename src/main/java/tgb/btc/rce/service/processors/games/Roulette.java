package tgb.btc.rce.service.processors.games;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.ROULETTE)
public class Roulette extends Processor {
    @Override
    public void run(Update update) {
        responseSender.sendBotMessage(BotMessageType.ROULETTE, updateService.getChatId(update));
    }
}
