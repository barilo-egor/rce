package tgb.btc.rce.service.processors.admin.requests.spamban;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserInfoService;


@CommandProcessor(command = Command.SHOW_SPAM_BANNED_USER)
public class ShowSpamBannedUser extends Processor {

    private UserInfoService userInfoService;

    private ISpamBanService spamBanService;

    @Autowired
    public void setSpamBanService(ISpamBanService spamBanService) {
        this.spamBanService = spamBanService;
    }

    @Autowired
    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Override
    public void run(Update update) {
        Long spamBanPid = callbackQueryService.getSplitLongData(update, 1);
        Long chatId = updateService.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        if (spamBanService.countByPid(spamBanPid) == 0) {
            responseSender.sendMessage(chatId, "Заявка уже обработана.");
            return;
        }
        userInfoService.sendSpamBannedUser(chatId, spamBanPid);
    }

}
