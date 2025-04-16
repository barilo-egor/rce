package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.process.ISendLogsService;

@Service
public class ArchiveLogsHandler implements ICallbackQueryHandler {

    private final ISendLogsService sendLogsService;

    public ArchiveLogsHandler(ISendLogsService sendLogsService) {
        this.sendLogsService = sendLogsService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        sendLogsService.send(callbackQuery.getMessage().getChatId(), true);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ARCHIVE_LOGS;
    }
}
