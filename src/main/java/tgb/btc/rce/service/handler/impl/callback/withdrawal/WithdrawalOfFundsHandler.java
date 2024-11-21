package tgb.btc.rce.service.handler.impl.callback.withdrawal;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.interfaces.service.bean.bot.IWithdrawalRequestService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.util.List;

@Service
public class WithdrawalOfFundsHandler implements ICallbackQueryHandler {

    private final IWithdrawalRequestService withdrawalRequestService;

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    private final ICallbackDataService callbackDataService;

    private final VariablePropertiesReader variablePropertiesReader;

    private final IReadUserService readUserService;

    private final IModifyUserService modifyUserService;

    private final IMessagePropertiesService messagePropertiesService;

    private final IRedisUserStateService redisUserStateService;

    public WithdrawalOfFundsHandler(IWithdrawalRequestService withdrawalRequestService, IResponseSender responseSender,
                                    IKeyboardBuildService keyboardBuildService, ICallbackDataService callbackDataService,
                                    VariablePropertiesReader variablePropertiesReader, IReadUserService readUserService,
                                    IModifyUserService modifyUserService, IMessagePropertiesService messagePropertiesService,
                                    IRedisUserStateService redisUserStateService) {
        this.withdrawalRequestService = withdrawalRequestService;
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
        this.callbackDataService = callbackDataService;
        this.variablePropertiesReader = variablePropertiesReader;
        this.readUserService = readUserService;
        this.modifyUserService = modifyUserService;
        this.messagePropertiesService = messagePropertiesService;
        this.redisUserStateService = redisUserStateService;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        if (withdrawalRequestService.getActiveByUserChatId(chatId) > 0) {
            responseSender.sendMessage(chatId, "У вас уже есть активная заявка.",
                    keyboardBuildService.buildInline(List.of(
                            InlineButton.builder()
                                    .text("Удалить")
                                    .data(callbackDataService.buildData(
                                            CallbackQueryData.DELETE_WITHDRAWAL_REQUEST,
                                            withdrawalRequestService.getPidByUserChatId(chatId)
                                    ))
                                    .build()
                    )));
            return;
        }
        int minSum = variablePropertiesReader.getInt(VariableType.REFERRAL_MIN_SUM);
        if (readUserService.getReferralBalanceByChatId(chatId) < minSum) {
            responseSender.sendMessage(chatId, "Минимальная сумма для вывода средств равна " + minSum + "₽");
            return;
        }
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        redisUserStateService.save(chatId, UserState.WITHDRAWAL_OF_FUNDS);
        ReplyKeyboard keyboard = keyboardBuildService.buildReply(List.of(
                ReplyButton.builder()
                        .text("Поделиться контактом")
                        .isRequestContact(true)
                        .isRequestLocation(false)
                        .build(),
                BotReplyButton.CANCEL.getButton()
        ));
        responseSender.sendMessage(chatId, messagePropertiesService.getMessage(PropertiesMessage.WITHDRAWAL_ASK_CONTACT),
                keyboard);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.WITHDRAWAL_OF_FUNDS;
    }
}
