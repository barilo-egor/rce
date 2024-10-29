package tgb.btc.rce.service.processors.tool;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.INLINE_DELETE)
public class InlineDelete extends Processor {
    @Override
    public void run(Update update) {
        responseSender.deleteMessage(updateService.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
    }
}
