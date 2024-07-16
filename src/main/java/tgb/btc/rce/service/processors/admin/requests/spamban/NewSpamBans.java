package tgb.btc.rce.service.processors.admin.requests.spamban;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.interfaces.service.bean.bot.ISpamBanService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserInfoService;

import java.util.List;

@CommandProcessor(command = Command.NEW_SPAM_BANS)
public class NewSpamBans extends Processor {

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
        Long chatId = updateService.getChatId(update);
        List<Long> pids = spamBanService.getPids();
        if (CollectionUtils.isEmpty(pids)) {
            responseSender.sendMessage(chatId, "Список пуст.");
            return;
        }
        for (Long spamBanPid : pids) {
            userInfoService.sendSpamBannedUser(chatId, spamBanPid);
        }
    }

}
