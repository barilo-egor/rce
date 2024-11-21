package tgb.btc.rce.service.handler.impl.message.text.state;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.message.text.IStateTextMessageHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class UserReferralBalanceStateHandler implements IStateTextMessageHandler {

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    private final IAdminPanelService adminPanelService;

    private final IRedisUserStateService redisUserStateService;

    public UserReferralBalanceStateHandler(IReadUserService readUserService, IResponseSender responseSender,
                                           IKeyboardBuildService keyboardBuildService,
                                           IAdminPanelService adminPanelService, IRedisUserStateService redisUserStateService) {
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
        this.adminPanelService = adminPanelService;
        this.redisUserStateService = redisUserStateService;
    }

    @Override
    public void handle(Message message) {
        Long chatId = message.getChatId();
        if (message.getText().equals(BotReplyButton.CANCEL.getText())) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        Long userChatId = Long.parseLong(message.getText());
        if (readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "У пользователя с чат айди " + userChatId
                            + " на реферальном балансе " + readUserService.getReferralBalanceByChatId(userChatId) + "₽",
                    keyboardBuildService.buildInline(List.of(
                            InlineButton.builder()
                                    .inlineType(InlineType.CALLBACK_DATA)
                                    .text("Изменить")
                                    .data(Command.CHANGE_REFERRAL_BALANCE.name()
                                            + BotStringConstants.CALLBACK_DATA_SPLITTER + userChatId)
                                    .build()
                    )));
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
        } else responseSender.sendMessage(chatId, "Пользователь не найден.");
    }

    @Override
    public UserState getUserState() {
        return UserState.USER_REFERRAL_BALANCE;
    }
}
