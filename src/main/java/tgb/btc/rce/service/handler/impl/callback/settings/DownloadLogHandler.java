package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.io.File;

@Service
public class DownloadLogHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public DownloadLogHandler(IResponseSender responseSender, ICallbackDataService callbackDataService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String path = callbackDataService.getArgument(callbackQuery.getData(), 1);
        File log = new File(path);
        if (log.length() == 0) {
            responseSender.sendMessage(chatId, "Лог пуст.");
        }
        responseSender.sendFile(chatId, log);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DOWNLOAD_LOG;
    }
}
