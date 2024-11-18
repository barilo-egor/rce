package tgb.btc.rce.service.handler.impl.message.text.slash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.SlashCommand;
import tgb.btc.rce.service.handler.message.text.ISlashCommandHandler;
import tgb.btc.rce.service.handler.service.IStartService;

@Service
@Slf4j
public class StartHandler implements ISlashCommandHandler {

    private final IStartService startService;

    public StartHandler(IStartService startService) {
        this.startService = startService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        startService.process(chatId);
    }

    @Override
    public SlashCommand getSlashCommand() {
        return SlashCommand.START;
    }
}
