package tgb.btc.rce.service.processors.admin.settings.paymenttypes.requisite.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.handler.util.IShowRequisitesService;

@CommandProcessor(command = Command.DELETE_PAYMENT_TYPE_REQUISITE, step = 2)
public class ShowRequisitesForDelete extends Processor {
    private IShowRequisitesService showRequisitesService;

    @Autowired
    public void setShowRequisitesService(IShowRequisitesService showRequisitesService) {
        this.showRequisitesService = showRequisitesService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выберите тип оплаты.");
            return;
        }
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        showRequisitesService.showForDelete(chatId, Long.parseLong(values[1]));
        processToAdminMainPanel(chatId);
    }
}
