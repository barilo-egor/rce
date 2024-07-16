package tgb.btc.rce.service.processors.admin.requests.spamban;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;


@CommandProcessor(command = Command.KEEP_SPAM_BAN)
@Slf4j
public class KeepSpamBan extends Processor {

    private ISpamBanService spamBanService;

    @Autowired
    public void setSpamBanService(ISpamBanService spamBanService) {
        this.spamBanService = spamBanService;
    }

    @Override
    public void run(Update update) {
        Long spamBanPid = callbackQueryService.getSplitLongData(update, 1);
        Long chatId = updateService.getChatId(update);
        Long userChatId = spamBanService.getUserChatIdByPid(spamBanPid);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        spamBanService.deleteById(spamBanPid);
        responseSender.sendMessage(chatId, "Пользователь " + userChatId + " останется в бане.");
        log.debug("Админ chatId={} оставил пользователя chatId={} в бане после спам блокировки.", chatId, userChatId);
    }

}
