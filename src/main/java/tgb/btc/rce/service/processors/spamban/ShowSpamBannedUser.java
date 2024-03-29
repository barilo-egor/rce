package tgb.btc.rce.service.processors.spamban;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.repository.bot.SpamBanRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserInfoService;
import tgb.btc.library.service.bean.bot.UserService;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

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
