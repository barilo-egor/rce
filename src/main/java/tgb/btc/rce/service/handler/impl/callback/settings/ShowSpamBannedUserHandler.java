package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IUserInfoService;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class ShowSpamBannedUserHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final ISpamBanService spamBanService;

    private final IUserInfoService userInfoService;

    public ShowSpamBannedUserHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                     ISpamBanService spamBanService, IUserInfoService userInfoService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.spamBanService = spamBanService;
        this.userInfoService = userInfoService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long spamBanPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        if (spamBanService.countByPid(spamBanPid) == 0) {
            responseSender.sendMessage(chatId, "Заявка уже обработана.");
            return;
        }
        userInfoService.sendSpamBannedUser(chatId, spamBanPid);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SHOW_SPAM_BANNED_USER;
    }
}
