package tgb.btc.rce.service.processors.support;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.WithdrawalRequest;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.impl.WithdrawalRequestService;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@Service
public class WithdrawalOfFundsService {

    private final UserService userService;
    private final IResponseSender responseSender;
    private final WithdrawalRequestService withdrawalRequestService;
    private final AdminService adminService;

    @Autowired
    public WithdrawalOfFundsService(UserService userService, IResponseSender responseSender,
                                    WithdrawalRequestService withdrawalRequestService, AdminService adminService) {
        this.userService = userService;
        this.responseSender = responseSender;
        this.withdrawalRequestService = withdrawalRequestService;
        this.adminService = adminService;
    }

    public boolean isBalanceLessThanMinSum(Long chatId) {
        return userService.getReferralBalanceByChatId(chatId) <
                BotVariablePropertiesUtil.getInt(BotVariableType.MIN_WITHDRAWAL_OF_FUNDS_SUM);
    }

    public void sendMinSumMessage(Long chatId) {
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_MIN_SUM));
    }

    public void askForSum(Integer messageId, Long chatId) {
        responseSender.deleteMessage(chatId, messageId);
        String message = MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_ASK_SUM) + "\n"
                + MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_MIN_SUM);
        userService.nextStep(chatId);
        responseSender.sendMessage(chatId, message, KeyboardUtil.buildReply(
                List.of(ReplyButton.builder()
                        .text(Command.CANCEL.getText())
                        .build()),
                false));
    }

    public boolean createRequest(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Integer inputSum = NumberUtil.getInputInt(UpdateUtil.getMessageText(update));
        if (inputSum < BotVariablePropertiesUtil.getMinSumForWithdrawal()) {
            responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_MIN_SUM));
            return false;
        }
        int reserve = withdrawalRequestService.getCreatedTotalSumByChatId(chatId);
        int balanceWithoutReserve = userService.getReferralBalanceByChatId(chatId) - reserve;
        if (balanceWithoutReserve < BotVariablePropertiesUtil.getMinSumForWithdrawal()) {
            String reserveText = reserve > 0 ? "В резерве " + reserve + " руб." : Strings.EMPTY;
            responseSender.sendMessage(chatId,
                    String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.INSUFFICIENT_FUNDS), reserveText));
            return false;
        }
        WithdrawalRequest request = withdrawalRequestService.save(
                WithdrawalRequest.buildFromUpdate(userService.findByChatId(update), inputSum));
        adminService.notify(MessagePropertiesUtil.getMessage(PropertiesMessage.ADMIN_NOTIFY_WITHDRAWAL_NEW),
                Command.SHOW_WITHDRAWAL_REQUEST.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER +
                        request.getPid());
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.USER_RESPONSE_WITHDRAWAL_REQUEST_CREATED));
        return true;
    }
}
