package tgb.btc.rce.service.impl.process;

import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.process.ISendLogsService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class SendLogsService implements ISendLogsService {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    public SendLogsService(IResponseSender responseSender, ICallbackDataService callbackDataService,
                           IKeyboardBuildService keyboardBuildService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void send(Long chatId, boolean isArchive) {
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            responseSender.sendMessage(chatId, "Отсутствует директория <b>logs</b>.");
            return;
        }
        File[] files = logsDir.listFiles();
        if (files == null || files.length == 0) {
            responseSender.sendMessage(chatId, "В директории <b>logs</b> отсутствуют файлы.");
            return;
        }
        List<File> fileList = Arrays.asList(files);
        fileList.sort(Comparator.comparing(File::getName));
        List<InlineButton> buttons = new ArrayList<>();
        Pattern datePattern = Pattern.compile(".*\\d{4}-\\d{2}-\\d{2}.*");
        for (File file : fileList) {
            boolean isMatch = datePattern.matcher(file.getName()).matches();
            if (file.isFile() && ((isArchive && isMatch) || (!isArchive && !isMatch))) {
                buttons.add(
                        InlineButton.builder()
                                .text(file.getName())
                                .data(callbackDataService.buildData(CallbackQueryData.DOWNLOAD_LOG, file.getPath()))
                                .build()
                );
            }
        }
        String message = isArchive
                ? "Доступные для скачивания архивные логи:"
                : "Доступные для скачивания логи:";
        if (!isArchive) {
            buttons.add(InlineButton.builder().text("Архивные логи").data(CallbackQueryData.ARCHIVE_LOGS.name()).build());
        }
        responseSender.sendMessage(chatId, message, keyboardBuildService.buildInline(buttons, 2));
    }
}
