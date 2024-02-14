package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.service.Processor;
import tgb.btc.library.service.bean.bot.WithdrawalRequestService;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.SHOW_WITHDRAWAL_REQUEST)
public class ShowWithdrawalRequest extends Processor {

    private WithdrawalOfFundsService withdrawalOfFundsService;

    private WithdrawalRequestService withdrawalRequestService;

    @Autowired
    public void setWithdrawalOfFundsService(WithdrawalOfFundsService withdrawalOfFundsService) {
        this.withdrawalOfFundsService = withdrawalOfFundsService;
    }

    @Autowired
    public void setWithdrawalRequestService(WithdrawalRequestService withdrawalRequestService) {
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
        responseSender.sendMessage(chatId, withdrawalOfFundsService.toString(
                        withdrawalRequestService.findById(CallbackQueryUtil.getSplitLongData(update, 1))),
                KeyboardUtil.buildInline(List.of(InlineButton.builder()
                        .text("Скрыть")
                        .data(Command.HIDE_WITHDRAWAL.name() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                + update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1])
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build())));
    }
}
