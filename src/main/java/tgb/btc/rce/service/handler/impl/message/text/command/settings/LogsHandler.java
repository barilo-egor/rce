package tgb.btc.rce.service.handler.impl.message.text.command.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;
import tgb.btc.rce.service.process.ISendLogsService;

@Service
public class LogsHandler implements ITextCommandHandler {

    private final ISendLogsService sendLogsService;

    public LogsHandler(ISendLogsService sendLogsService) {
        this.sendLogsService = sendLogsService;
    }

    @Override
    public void handle(Message message) {
        sendLogsService.send(message.getChatId(), false);
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.LOGS;
    }
}
