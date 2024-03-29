package tgb.btc.rce.service.processors.referralpercent;

import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.MessageTemplate;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.REFERRAL_PERCENT)
public class ReferralPercent extends Processor {

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.sendMessage(chatId, MessageTemplate.ASK_CHAT_ID);
        userRepository.nextStep(chatId, Command.REFERRAL_PERCENT.name());
    }
}
