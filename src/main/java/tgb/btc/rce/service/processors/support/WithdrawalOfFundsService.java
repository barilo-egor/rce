package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.User;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.bot.WithdrawalRequestStatus;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

import java.util.Set;

@Service
public class WithdrawalOfFundsService {

    private IResponseSender responseSender;

    private IWithdrawalRequestService withdrawalRequestService;

    private INotifyService notifyService;

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    private IKeyboardBuildService keyboardBuildService;

    private IMessagePropertiesService messagePropertiesService;
    
    private IUpdateService updateService;

    private ICommandService commandService;

    private ICallbackDataService callbackDataService;

    @Autowired
    public void setCallbackDataService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Autowired
    public void setCommandService(ICommandService commandService) {
        this.commandService = commandService;
    }

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setNotifyService(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setWithdrawalRequestService(IWithdrawalRequestService withdrawalRequestService) {
        this.withdrawalRequestService = withdrawalRequestService;
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
        Long chatId = updateService.getChatId(update);
        if (!update.getMessage().hasContact()) {
            responseSender.sendMessage(chatId,
                    messagePropertiesService.getMessage(PropertiesMessage.WITHDRAWAL_ERROR_CONTACT));
            return false;
        }
        WithdrawalRequest request = withdrawalRequestService.save(
                buildFromUpdate(readUserService.findByChatId(updateService.getChatId(update)), update));
        notifyService.notifyMessage(messagePropertiesService.getMessage(PropertiesMessage.ADMIN_NOTIFY_WITHDRAWAL_NEW),
                callbackDataService.buildData(CallbackQueryData.SHOW_WITHDRAWAL_REQUEST, request.getPid()),
                Set.of(UserRole.OPERATOR, UserRole.ADMIN));
        responseSender.sendMessage(chatId,
                messagePropertiesService.getMessage(PropertiesMessage.USER_RESPONSE_WITHDRAWAL_REQUEST_CREATED));
        return true;
    }

    public WithdrawalRequest buildFromUpdate(User user, Update update) {
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setUser(user);
        withdrawalRequest.setStatus(WithdrawalRequestStatus.CREATED);
        withdrawalRequest.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
        withdrawalRequest.setActive(true);
        return withdrawalRequest;
    }

    public String toString(WithdrawalRequest withdrawalRequest) {
        return String.format(messagePropertiesService.getMessage(PropertiesMessage.WITHDRAWAL_TO_STRING),
                withdrawalRequest.getPid(), withdrawalRequest.getPhoneNumber(), withdrawalRequest.getUser().getChatId());
    }
}
