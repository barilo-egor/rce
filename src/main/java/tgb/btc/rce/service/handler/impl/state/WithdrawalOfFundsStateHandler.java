package tgb.btc.rce.service.handler.impl.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.bot.WithdrawalRequestStatus;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

@Service
public class WithdrawalOfFundsStateHandler implements IStateHandler {

    private final static String ERROR_MESSAGE = "Нажми \"Поделиться контактом\" чтобы отправить нам контакт, либо \"Отмена\".";

    private final IStartService startService;

    private final IResponseSender responseSender;

    private final IWithdrawalRequestService withdrawalRequestService;

    private final IReadUserService readUserService;

    private final INotifyService notifyService;

    private final ICallbackDataService callbackDataService;

    private final IMessagePropertiesService messagePropertiesService;

    public WithdrawalOfFundsStateHandler(IStartService startService, IResponseSender responseSender,
                                         IWithdrawalRequestService withdrawalRequestService,
                                         IReadUserService readUserService, INotifyService notifyService,
                                         ICallbackDataService callbackDataService,
                                         IMessagePropertiesService messagePropertiesService) {
        this.startService = startService;
        this.responseSender = responseSender;
        this.withdrawalRequestService = withdrawalRequestService;
        this.readUserService = readUserService;
        this.notifyService = notifyService;
        this.callbackDataService = callbackDataService;
        this.messagePropertiesService = messagePropertiesService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage()) {
            sendErrorMessage(UpdateType.getChatId(update));
            return;
        }
        Message message = update.getMessage();
        if (message.hasText() && message.getText().equals(TextCommand.CANCEL.getText())) {
            startService.processToMainMenu(message.getChatId());
            return;
        }
        if (!message.hasContact()) {
            sendErrorMessage(UpdateType.getChatId(update));
            return;
        }
        Long chatId = message.getChatId();
        WithdrawalRequest request = new WithdrawalRequest();
        request.setUser(readUserService.findByChatId(message.getChatId()));
        request.setStatus(WithdrawalRequestStatus.CREATED);
        request.setPhoneNumber(message.getContact().getPhoneNumber());
        request.setActive(true);
        withdrawalRequestService.save(request);
        notifyService.notifyMessage(messagePropertiesService.getMessage(PropertiesMessage.ADMIN_NOTIFY_WITHDRAWAL_NEW),
                callbackDataService.buildData(CallbackQueryData.SHOW_WITHDRAWAL_REQUEST, request.getPid()),
                UserRole.OPERATOR_ACCESS);
        responseSender.sendMessage(chatId,
                messagePropertiesService.getMessage(PropertiesMessage.USER_RESPONSE_WITHDRAWAL_REQUEST_CREATED));
        startService.processToMainMenu(chatId);
    }

    private void sendErrorMessage(Long chatId) {
        responseSender.sendMessage(chatId, ERROR_MESSAGE);
    }

    @Override
    public UserState getUserState() {
        return UserState.WITHDRAWAL_OF_FUNDS;
    }
}
