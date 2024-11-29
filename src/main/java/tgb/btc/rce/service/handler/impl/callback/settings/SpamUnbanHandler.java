package tgb.btc.rce.service.handler.impl.callback.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class SpamUnbanHandler implements ICallbackQueryHandler {

    private final BanningUserService banningUserService;

    private final ISpamBanService spamBanService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public SpamUnbanHandler(BanningUserService banningUserService, ISpamBanService spamBanService,
                            IResponseSender responseSender, ICallbackDataService callbackDataService) {
        this.banningUserService = banningUserService;
        this.spamBanService = spamBanService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long spamBanPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Long chatId = callbackQuery.getFrom().getId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Long userChatId = spamBanService.getUserChatIdByPid(spamBanPid);
        banningUserService.unban(userChatId);
        spamBanService.deleteById(spamBanPid);
        responseSender.sendMessage(userChatId,
                "Вы были разблокированы из спам блока администратором.");
        responseSender.sendMessage(chatId, "Пользователь " + userChatId + " был разблокирован.");
        log.debug("Админ chatId={} разблокировал пользователя {} после спам блокировки.", chatId, userChatId);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SPAM_UNBAN;
    }
}
