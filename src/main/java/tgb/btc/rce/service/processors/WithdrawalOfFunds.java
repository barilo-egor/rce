package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.processors.support.WithdrawalOfFundsService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.WITHDRAWAL_OF_FUNDS)
public class WithdrawalOfFunds extends Processor {

    private WithdrawalOfFundsService withdrawalOfFundsService;

    private IWithdrawalRequestService withdrawalRequestService;

    private VariablePropertiesReader variablePropertiesReader;

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setWithdrawalOfFundsService(WithdrawalOfFundsService withdrawalOfFundsService) {
        this.withdrawalOfFundsService = withdrawalOfFundsService;
    }

    @Autowired
    public void setWithdrawalRequestService(IWithdrawalRequestService withdrawalRequestService) {
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        if (checkForCancel(update)) return;
        switch (readUserService.getStepByChatId(chatId)) {
            case 0:
                if (withdrawalRequestService.getActiveByUserChatId(chatId) > 0) {
                    responseSender.sendMessage(chatId, "У вас уже есть активная заявка.",
                            keyboardBuildService.buildInline(List.of(
                                    InlineButton.builder()
                                            .text("Удалить")
                                            .data(Command.DELETE_WITHDRAWAL_REQUEST.getText()
                                                    + BotStringConstants.CALLBACK_DATA_SPLITTER
                                                    + withdrawalRequestService.getPidByUserChatId(chatId))
                                            .build()
                            )));
                    return;
                }
                int minSum = variablePropertiesReader.getInt(VariableType.REFERRAL_MIN_SUM);
                if (readUserService.getReferralBalanceByChatId(chatId) < minSum) {
                    responseSender.sendMessage(chatId, "Минимальная сумма для вывода средств равна " + minSum + "₽");
                    return;
                }
                withdrawalOfFundsService.askForContact(chatId, updateService.getMessage(update).getMessageId());
                break;
            case 1:
                if (withdrawalOfFundsService.createRequest(update)) processToMainMenu(chatId);
                break;
        }
    }
}
