package tgb.btc.rce.service.processors.spamban;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.repository.bot.SpamBanRepository;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.CallbackQueryUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.KEEP_SPAM_BAN)
@Slf4j
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
        log.debug("Админ chatId={} оставил пользователя chatId={} в бане после спам блокировки.", chatId, userChatId);
    }

}
