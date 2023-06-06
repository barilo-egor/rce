package tgb.btc.lib.service.processors.spamban;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.SpamBanRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.CallbackQueryUtil;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.KEEP_SPAM_BAN)
public class KeepSpamBan extends Processor {

    private SpamBanRepository spamBanRepository;

    @Autowired
    public void setSpamBanRepository(SpamBanRepository spamBanRepository) {
        this.spamBanRepository = spamBanRepository;
    }

    @Override
    public void run(Update update) {
        Long spamBanPid = CallbackQueryUtil.getSplitLongData(update, 1);
        Long chatId = UpdateUtil.getChatId(update);
        Long userChatId = spamBanRepository.getUserChatIdByPid(spamBanPid);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        spamBanRepository.deleteById(spamBanPid);
        responseSender.sendMessage(chatId, "Пользователь " + userChatId + " останется в бане.");
    }

}
