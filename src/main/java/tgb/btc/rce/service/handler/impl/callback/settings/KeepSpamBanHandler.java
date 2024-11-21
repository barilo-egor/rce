package tgb.btc.rce.service.handler.impl.callback.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.callback.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
@Slf4j
public class KeepSpamBanHandler implements ICallbackQueryHandler {

    private final ISpamBanService spamBanService;

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    public KeepSpamBanHandler(ISpamBanService spamBanService, IResponseSender responseSender,
                              ICallbackDataService callbackDataService) {
        this.spamBanService = spamBanService;
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long spamBanPid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Long chatId = callbackQuery.getFrom().getId();
        Long userChatId = spamBanService.getUserChatIdByPid(spamBanPid);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        spamBanService.deleteById(spamBanPid);
        responseSender.sendMessage(chatId, "Пользователь " + userChatId + " останется в бане.");
        log.debug("Админ chatId={} оставил пользователя chatId={} в бане после спам блокировки.", chatId, userChatId);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.KEEP_SPAM_BAN;
    }
}
