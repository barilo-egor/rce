package tgb.btc.rce.service.processors.spamban;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.repository.bot.SpamBanRepository;
import tgb.btc.library.service.process.BanningUserService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.SPAM_UNBAN)
public class SpamUnban extends Processor {

    private SpamBanRepository spamBanRepository;

    private BanningUserService banningUserService;

    @Autowired
    public void setBanningUserService(BanningUserService banningUserService) {
        this.banningUserService = banningUserService;
    }

    @Autowired
    public void setSpamBanRepository(SpamBanRepository spamBanRepository) {
        this.spamBanRepository = spamBanRepository;
    }

    @Override
    public void run(Update update) {
        Long spamBanPid = CallbackQueryUtil.getSplitLongData(update, 1);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        Long userChatId = spamBanRepository.getUserChatIdByPid(spamBanPid);
        banningUserService.unban(userChatId);
        spamBanRepository.deleteById(spamBanPid);
        responseSender.sendMessage(userChatId,
                                   "Вы были разблокированы из спам блока администратором.");
        responseSender.sendMessage(chatId, "Пользователь " + userChatId + " был разблокирован.");
    }

}
