package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.sender.IResponseSender;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.util.WithdrawalRequestUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@Service
public class WithdrawalOfFundsService {

    private IResponseSender responseSender;

    private IWithdrawalRequestService withdrawalRequestService;

    private AdminService adminService;

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setWithdrawalRequestService(IWithdrawalRequestService withdrawalRequestService) {
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    public boolean createRequest(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.getMessage().hasContact()) {
            responseSender.sendMessage(chatId,
                    MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_ERROR_CONTACT));
            return false;
        }
        WithdrawalRequest request = withdrawalRequestService.save(
                WithdrawalRequestUtil.buildFromUpdate(readUserService.findByChatId(UpdateUtil.getChatId(update)), update));
        adminService.notify(MessagePropertiesUtil.getMessage(PropertiesMessage.ADMIN_NOTIFY_WITHDRAWAL_NEW),
                Command.SHOW_WITHDRAWAL_REQUEST.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER +
                        request.getPid());
        responseSender.sendMessage(chatId,
                MessagePropertiesUtil.getMessage(PropertiesMessage.USER_RESPONSE_WITHDRAWAL_REQUEST_CREATED));
        return true;
    }

    public String toString(WithdrawalRequest withdrawalRequest) {
        return String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_TO_STRING),
                withdrawalRequest.getPid(), withdrawalRequest.getPhoneNumber(), withdrawalRequest.getUser().getChatId());
    }

    public void askForContact(Long chatId, Integer messageId) {
        responseSender.deleteMessage(chatId, messageId);
        modifyUserService.nextStep(chatId, Command.WITHDRAWAL_OF_FUNDS.name());
        ReplyKeyboard keyboard = KeyboardUtil.buildReply(List.of(
                ReplyButton.builder()
                        .text(Command.SHARE_CONTACT.getText())
                        .isRequestContact(true)
                        .isRequestLocation(false)
                        .build(),
                ReplyButton.builder()
                        .text(Command.CANCEL.getText())
                        .build()
        ));
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_ASK_CONTACT),
                keyboard);
    }
}
