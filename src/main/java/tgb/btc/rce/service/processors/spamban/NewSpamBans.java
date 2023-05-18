package tgb.btc.rce.service.processors.spamban;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.SpamBanRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserInfoService;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;

@CommandProcessor(command = Command.NEW_SPAM_BANS)
public class NewSpamBans extends Processor {

    private SpamBanRepository spamBanRepository;

    private UserInfoService userInfoService;

    @Autowired
    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Autowired
    public void setSpamBanRepository(SpamBanRepository spamBanRepository) {
        this.spamBanRepository = spamBanRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        List<Long> pids = spamBanRepository.getPids();
        if (CollectionUtils.isEmpty(pids)) {
            responseSender.sendMessage(chatId, "Список пуст.");
            return;
        }
        for (Long spamBanPid : pids) {
            userInfoService.sendSpamBannedUser(chatId, spamBanPid);
        }
    }

}
