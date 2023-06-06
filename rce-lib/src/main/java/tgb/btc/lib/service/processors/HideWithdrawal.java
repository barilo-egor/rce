package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.WithdrawalRequestService;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.HIDE_WITHDRAWAL)
public class HideWithdrawal extends Processor {

    private WithdrawalRequestService withdrawalRequestService;

    @Autowired
    public void setWithdrawalRequestService(WithdrawalRequestService withdrawalRequestService) {
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        withdrawalRequestService.updateIsActiveByPid(false,
                Long.parseLong(update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));
        responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
    }
}
