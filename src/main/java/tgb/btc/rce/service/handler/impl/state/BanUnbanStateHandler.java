package tgb.btc.rce.service.handler.impl.state;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.rce.enums.BotReplyButton;
import tgb.btc.rce.enums.Menu;
import tgb.btc.rce.enums.UserState;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.enums.update.TextCommand;
import tgb.btc.rce.enums.update.UpdateType;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IRedisUserStateService;
import tgb.btc.rce.service.handler.IStateHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.service.util.IMenuService;
import tgb.btc.rce.vo.InlineButton;

@Service
@Slf4j
public class BanUnbanStateHandler implements IStateHandler {

    private final IReadUserService readUserService;

    private final IResponseSender responseSender;

    private final IMenuService menuService;

    private final IAdminPanelService adminPanelService;

    private final BannedUserCache bannedUserCache;

    private final IRedisUserStateService redisUserStateService;

    private final ICallbackDataService callbackDataService;

    public BanUnbanStateHandler(IReadUserService readUserService, IResponseSender responseSender,
                                IMenuService menuService, IAdminPanelService adminPanelService,
                                BannedUserCache bannedUserCache,
                                IRedisUserStateService redisUserStateService, ICallbackDataService callbackDataService) {
        this.readUserService = readUserService;
        this.responseSender = responseSender;
        this.menuService = menuService;
        this.adminPanelService = adminPanelService;
        this.bannedUserCache = bannedUserCache;
        this.redisUserStateService = redisUserStateService;
        this.callbackDataService = callbackDataService;
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
        if (!message.getText().equals(BotReplyButton.CANCEL.getText())) {
            Long inputChatId = Long.parseLong(message.getText());
            if (!readUserService.existsByChatId(inputChatId)) {
                responseSender.sendMessage(chatId, "Пользователь с таким ID не найден.",
                        menuService.build(Menu.ADMIN_BACK, readUserService.getUserRoleByChatId(chatId)));
                return;
            }
            boolean isBanned = BooleanUtils.isTrue(bannedUserCache.get(inputChatId));
            String text = "Пользователь с chat id <b>" + inputChatId + "</b> на текущий момент <b>"
                    + (isBanned ? "заблокирован" : "не заблокирован") + "</b>.";
            InlineButton inlineButton = InlineButton.builder()
                    .text(isBanned ? "Разблокировать" : "Заблокировать")
                    .data(callbackDataService.buildData(CallbackQueryData.BAN_UNBAN, inputChatId))
                    .build();
            responseSender.sendMessage(chatId, text, inlineButton);
        }
        redisUserStateService.delete(chatId);
        adminPanelService.send(chatId);
    }

    @Override
    public UserState getUserState() {
        return UserState.BAN_UNBAN;
    }
}
