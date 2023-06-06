package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.InlineType;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.WithdrawalRequestService;
import tgb.btc.lib.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.lib.util.CallbackQueryUtil;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;

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
                        .data(Command.HIDE_WITHDRAWAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                + update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1])
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build())));
    }
}
