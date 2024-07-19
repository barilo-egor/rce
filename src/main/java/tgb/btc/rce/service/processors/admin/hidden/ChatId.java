package tgb.btc.rce.service.processors.admin.hidden;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;

@CommandProcessor(command = Command.CHAT_ID)
public class ChatId extends Processor {
    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        responseSender.sendMessage(chatId, "Ваш chat id - <code>" + chatId + "</code>.\nНажмите на chat id для копирования в буфер обмена.", "html");
    }
}
