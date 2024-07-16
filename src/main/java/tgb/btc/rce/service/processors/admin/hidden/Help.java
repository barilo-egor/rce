package tgb.btc.rce.service.processors.admin.hidden;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.HelpCommand;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.HELP)
public class Help extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        StringBuilder text = new StringBuilder();
        text.append("<b>Нажмите на команду для копирования в буфер обмена.</b>\n\n");
        for (Command command : Command.HIDDEN_COMMANDS) {
            if (command.hasAccess(readUserService.getUserRoleByChatId(chatId))) {
                text.append("<code>").append(command.getText()).append("</code>").append(HelpCommand.getDescription(command)).append("\n");
            }
        }
        responseSender.sendMessage(chatId, text.toString(), "html");
    }

}
