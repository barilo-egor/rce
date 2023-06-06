package tgb.btc.lib.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.ReferralUser;
import tgb.btc.lib.enums.*;
import tgb.btc.lib.repository.DealRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.MessagePropertiesUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.REFERRAL)
public class Referral extends Processor {

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String startParameter = "?start=" + chatId;
        String refLink = BotProperties.BOT_CONFIG_PROPERTIES.getString("bot.link").concat(startParameter);
        String currentBalance = userService.getReferralBalanceByChatId(chatId).toString();
        List<ReferralUser> referralUsers = userService.getUserReferralsByChatId(chatId);
        String numberOfReferrals = String.valueOf(referralUsers.size());
        int numberOfActiveReferrals = (int) referralUsers.stream()
                .filter(usr -> dealRepository.getCountPassedByUserChatId(usr.getChatId()) > 0).count();

        Long dealsCount = dealRepository.getCountPassedByUserChatId(chatId);
        Rank rank = Rank.getByDealsNumber(dealsCount.intValue());
        String resultMessage = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.REFERRAL_MAIN),
                refLink, currentBalance, numberOfReferrals, numberOfActiveReferrals,
                userService.getChargesByChatId(chatId), dealsCount, rank.getSmile(), rank.getPercent()).concat("%");

        responseSender.sendMessage(chatId, resultMessage, KeyboardUtil.buildInline(getButtons(refLink)));
    }

    private List<InlineButton> getButtons(String refLink) {
        return List.of(InlineButton.builder()
                        .text("Пригласить друга")
                        .data(refLink)
                        .inlineType(InlineType.SWITCH_INLINE_QUERY)
                        .build(),
                InlineButton.builder()
                        .text("Вывод средств")
                        .data(Command.WITHDRAWAL_OF_FUNDS.getText())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build());
    }
}
