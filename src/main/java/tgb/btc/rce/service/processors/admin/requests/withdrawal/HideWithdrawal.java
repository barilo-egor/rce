package tgb.btc.rce.service.processors.admin.requests.withdrawal;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.HIDE_WITHDRAWAL)
public class HideWithdrawal extends Processor {

    private IWithdrawalRequestService withdrawalRequestService;

    @Autowired
    public void setWithdrawalRequestService(IWithdrawalRequestService withdrawalRequestService) {
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        withdrawalRequestService.updateIsActiveByPid(false,
                Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));
        responseSender.deleteMessage(updateService.getChatId(update), updateService.getMessage(update).getMessageId());
    }
}
