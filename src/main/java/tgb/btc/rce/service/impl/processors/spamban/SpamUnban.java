package tgb.btc.rce.service.impl.processors.spamban;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SPAM_UNBAN)
@Slf4j
public class SpamUnban extends Processor {

    private BanningUserService banningUserService;

    private ISpamBanService spamBanService;

    @Autowired
    public void setSpamBanService(ISpamBanService spamBanService) {
        this.spamBanService = spamBanService;
    }

    @Autowired
    public void setBanningUserService(BanningUserService banningUserService) {
        this.banningUserService = banningUserService;
    }

    @Override
    public void run(Update update) {
        Long spamBanPid = CallbackQueryUtil.getSplitLongData(update, 1);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        Long userChatId = spamBanService.getUserChatIdByPid(spamBanPid);
        banningUserService.unban(userChatId);
        spamBanService.deleteById(spamBanPid);
        responseSender.sendMessage(userChatId,
                                   "Вы были разблокированы из спам блока администратором.");
        responseSender.sendMessage(chatId, "Пользователь " + userChatId + " был разблокирован.");
        log.debug("Админ chatId={} разблокировал пользователя {} после спам блокировки.", chatId, userChatId);
    }

}
