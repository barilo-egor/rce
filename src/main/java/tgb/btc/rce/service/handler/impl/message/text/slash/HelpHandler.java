package tgb.btc.rce.service.handler.impl.message.text.slash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.HelpCommand;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;
import tgb.btc.rce.service.util.ICommandService;

@Service
@Slf4j
public class HelpHandler implements ISlashCommandHandler {

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final ICommandService commandService;

    public HelpHandler(IReadUserService readUserService, IResponseSender responseSender,
                       ICommandService commandService) {
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.commandService = commandService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        StringBuilder text = new StringBuilder();
        text.append("<b>Нажмите на команду для копирования в буфер обмена.</b>\n\n");
        UserRole role = readUserService.getUserRoleByChatId(chatId);
        for (SlashCommand slashCommand: SlashCommand.values()) {
            if (slashCommand.hasAccess(role)) {
                text.append("<code>").append(slashCommand.getText()).append("</code>")
                        .append(HelpCommand.getDescription(slashCommand)).append("\n");
            }
        }
        responseSender.sendMessage(chatId, text.toString(), "html");
    }

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.HELP;
    }
}
