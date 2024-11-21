package tgb.btc.rce.service.handler.impl.message.text.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.bean.bot.WithdrawalRequest;
import tgb.btc.library.constants.enums.bot.UserRole;
import tgb.btc.library.constants.enums.bot.WithdrawalRequestStatus;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.INotifyService;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.message.text.IStateTextMessageHandler;
import tgb.btc.rce.service.handler.util.IStartService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

@Service
public class WithdrawalOfFundsStateHandler implements IStateTextMessageHandler {

    private final IStartService startService;

    private final IResponseSender responseSender;

    private final IWithdrawalRequestService withdrawalRequestService;

    private final IReadUserService readUserService;

    private final INotifyService notifyService;

    private final ICallbackDataService callbackDataService;

    private final IMessagePropertiesService messagePropertiesService;

    private final IRedisUserStateService redisUserStateService;

    public WithdrawalOfFundsStateHandler(IStartService startService, IResponseSender responseSender,
                                         IWithdrawalRequestService withdrawalRequestService,
                                         IReadUserService readUserService, INotifyService notifyService,
                                         ICallbackDataService callbackDataService,
                                         IMessagePropertiesService messagePropertiesService,
                                         IRedisUserStateService redisUserStateService) {
        this.startService = startService;
        this.responseSender = responseSender;
        this.withdrawalRequestService = withdrawalRequestService;
        this.readUserService = readUserService;
        this.notifyService = notifyService;
        this.callbackDataService = callbackDataService;
        this.messagePropertiesService = messagePropertiesService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        if (!message.hasContact()) {
            if (message.hasText() && message.getText().equals(BotReplyButton.CANCEL.getText())) {
                startService.processToMainMenu(chatId);
                return;
            }
            responseSender.sendMessage(chatId, "Поделитесь контактом, либо нажмите \"" + BotReplyButton.CANCEL.getText() + "\".");
            return;
        }
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

    @Override
    public UserState getUserState() {
        return UserState.WITHDRAWAL_OF_FUNDS;
    }
}
