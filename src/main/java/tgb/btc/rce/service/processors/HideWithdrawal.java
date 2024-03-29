package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.library.service.bean.bot.WithdrawalRequestService;
import tgb.btc.rce.util.UpdateUtil;

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
