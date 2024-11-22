package tgb.btc.rce.service.handler.impl.message.text.command.settings.report;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.message.text.ITextCommandHandler;

@Service
public class DealReportsHandler implements ITextCommandHandler {

    private final IResponseSender responseSender;

    private final IKeyboardService keyboardService;

    public DealReportsHandler(IResponseSender responseSender, IKeyboardService keyboardService) {
        this.responseSender = responseSender;
        this.keyboardService = keyboardService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        responseSender.sendMessage(chatId, "Выберите период, за которых хотите выгрузить отчет по сделкам.",
                keyboardService.getDealReports());
    }

    @Override
    public TextCommand getTextCommand() {
        return TextCommand.DEAL_REPORTS;
    }
}
