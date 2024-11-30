package tgb.btc.rce.service.handler.impl.callback.users;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.service.process.BannedUserCache;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class BanUnbanCallbackHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final BannedUserCache bannedUserCache;

    private final BanningUserService banningUserService;

    public BanUnbanCallbackHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                   BannedUserCache bannedUserCache, BanningUserService banningUserService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.bannedUserCache = bannedUserCache;
        this.banningUserService = banningUserService;
    }


    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        Long userChatId = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        if (BooleanUtils.isNotTrue(bannedUserCache.get(userChatId))) {
            banningUserService.ban(userChatId);
            responseSender.sendMessage(chatId,
                    "Пользователь <b>" + userChatId + "</b> заблокирован.");
            log.debug("Админ {} забанил пользователя {}", chatId, userChatId);
        } else {
            banningUserService.unban(userChatId);
            responseSender.sendMessage(chatId,
                    "Пользователь <b>" + userChatId + "</b> разблокирован.");
            log.debug("Админ {} разбанил пользователя {}", chatId, userChatId);
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.BAN_UNBAN;
    }
}
