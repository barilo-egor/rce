package tgb.btc.lib.service.processors.spamban;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.SpamBanRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.service.impl.UserInfoService;
import tgb.btc.lib.service.impl.UserService;
import tgb.btc.lib.util.CallbackQueryUtil;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.SHOW_SPAM_BANNED_USER)
public class ShowSpamBannedUser extends Processor {

    private UserInfoService userInfoService;

    private SpamBanRepository spamBanRepository;

    @Autowired
    public void setSpamBanRepository(SpamBanRepository spamBanRepository) {
        this.spamBanRepository = spamBanRepository;
    }

    @Autowired
    public void setUserInfoService(UserInfoService userInfoService) {
        this.userInfoService = userInfoService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(Update update) {
        Long spamBanPid = CallbackQueryUtil.getSplitLongData(update, 1);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        if (spamBanRepository.countByPid(spamBanPid) == 0) {
            responseSender.sendMessage(chatId, "Заявка уже обработана.");
            return;
        }
        userInfoService.sendSpamBannedUser(chatId, spamBanPid);
    }

}
