package tgb.btc.lib.service.processors.spamban;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.SpamBanRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.UserInfoService;
import tgb.btc.lib.util.UpdateUtil;

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
