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
        return getReferralSumWithoutReserve(chatId) <
                BotVariablePropertiesUtil.getInt(BotVariableType.MIN_WITHDRAWAL_OF_FUNDS_SUM);
    }

    public void sendMinSumMessage(Long chatId) {
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_MIN_SUM));
    }

    public boolean createRequest(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.getMessage().hasContact()) {

        }

        WithdrawalRequest request = withdrawalRequestService.save(
                WithdrawalRequest.buildFromUpdate(userService.findByChatId(update)));
        adminService.notify(MessagePropertiesUtil.getMessage(PropertiesMessage.ADMIN_NOTIFY_WITHDRAWAL_NEW),
                Command.SHOW_WITHDRAWAL_REQUEST.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER +
                        request.getPid());
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.USER_RESPONSE_WITHDRAWAL_REQUEST_CREATED));
        return true;
    }

    public String toString(WithdrawalRequest withdrawalRequest) {

    }

    private int getReferralSumWithoutReserve(Long chatId) {
        return userService.getReferralBalanceByChatId(chatId) -
                withdrawalRequestService.getCreatedTotalSumByChatId(chatId);
    }

    public void askForContact(Long chatId, Integer messageId) {
        responseSender.deleteMessage(chatId, messageId);
        userService.nextStep(chatId);
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_ASK_CONTACT),
                KeyboardUtil.buildReply(List.of(
                                ReplyButton.builder()
                                        .text(Command.SHARE_CONTACT.getText())
                                        .isRequestContact(true)
                                        .build(),
                                ReplyButton.builder()
                                        .text(Command.CANCEL.getText())
                                        .isRequestContact(true)
                                        .build()),
                        false));
    }
}
