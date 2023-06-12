package tgb.btc.rce.service.processors;

import org.apache.commons.lang.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.HELP)
public class Help extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        StringBuilder text = new StringBuilder();
        for (Command command : Command.values()) {
            if (command.isAdmin() && command.isHidden()) text.append(command.getText()).append("\n");
        }
        responseSender.sendMessage(chatId, text.toString());
    }

}
