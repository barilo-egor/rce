package tgb.btc.rce.service.handler.impl.state;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.IUserDiscountService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.impl.keyboard.KeyboardBuildService;
import tgb.btc.rce.service.redis.IRedisUserStateService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@Service
public class RankDiscountStateHandler implements IStateHandler {

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final IUserDiscountService userDiscountService;

    private final KeyboardBuildService keyboardBuildService;

    private final ICallbackDataService callbackDataService;

    private final IRedisUserStateService redisUserStateService;

    private final IAdminPanelService adminPanelService;

    public RankDiscountStateHandler(IReadUserService readUserService, IResponseSender responseSender,
                                    IUserDiscountService userDiscountService, KeyboardBuildService keyboardBuildService,
                                    ICallbackDataService callbackDataService, IRedisUserStateService redisUserStateService,
                                    IAdminPanelService adminPanelService) {
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.userDiscountService = userDiscountService;
        this.keyboardBuildService = keyboardBuildService;
        this.callbackDataService = callbackDataService;
        this.redisUserStateService = redisUserStateService;
        this.adminPanelService = adminPanelService;
    }

    @Override
    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            responseSender.sendMessage(UpdateType.getChatId(update),
                    "Введите chat id пользователя, либо нажмите \"" + TextCommand.CANCEL.getText() + "\".");
            return;
        }
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        if (message.getText().equals(BotReplyButton.CANCEL.getText())) {
            redisUserStateService.delete(chatId);
            adminPanelService.send(chatId);
            return;
        }
        Long userChatId = Long.parseLong(message.getText());
        if (!readUserService.existsByChatId(userChatId)) {
            responseSender.sendMessage(chatId, "Пользователь не найден.");
            return;
        }
        boolean isRankDiscountOn = BooleanUtils.isTrue(userDiscountService.getRankDiscountByUserChatId(userChatId));
        responseSender.sendMessage(chatId, "Пользователь chat id=" + userChatId + ".",
                keyboardBuildService.buildInline(List.of(InlineButton.builder()
                        .text(isRankDiscountOn ? "Выключить" : "Включить")
                        .data(callbackDataService.buildData(CallbackQueryData.CHANGE_RANK_DISCOUNT, userChatId, !isRankDiscountOn))
                        .build())));
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.RANK_DISCOUNT;
    }
}
