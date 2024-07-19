package tgb.btc.rce.service.processors;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.BotMessageType;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.BOT_OFFED)
public class BotOffed extends Processor {

    @Override
    public void run(Update update) {
        responseSender.sendBotMessage(BotMessageType.BOT_OFF, updateService.getChatId(update));
    }
}
