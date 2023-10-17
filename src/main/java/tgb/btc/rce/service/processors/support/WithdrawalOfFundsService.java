package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.WithdrawalRequest;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.AdminService;
import tgb.btc.rce.service.impl.bean.UserService;
import tgb.btc.rce.service.impl.bean.WithdrawalRequestService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@Service
public class WithdrawalOfFundsService {

    private final UserService userService;
    private final IResponseSender responseSender;
    private final WithdrawalRequestService withdrawalRequestService;
    private final AdminService adminService;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public WithdrawalOfFundsService(UserService userService, IResponseSender responseSender,
                                    WithdrawalRequestService withdrawalRequestService, AdminService adminService) {
        this.userService = userService;
        this.responseSender = responseSender;
        this.withdrawalRequestService = withdrawalRequestService;
        this.adminService = adminService;
    }

    public boolean createRequest(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.getMessage().hasContact()) {
            responseSender.sendMessage(chatId,
                    MessagePropertiesUtil.getMessage(PropertiesMessage.WITHDRAWAL_ERROR_CONTACT));
            return false;
        }
        WithdrawalRequest request = withdrawalRequestService.save(
                WithdrawalRequest.buildFromUpdate(userRepository.findByChatId(UpdateUtil.getChatId(update)), update));
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
        userService.nextStep(chatId, Command.WITHDRAWAL_OF_FUNDS);
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
