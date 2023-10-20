package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.ReferralUser;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
        String refLink = BotProperties.BOT_CONFIG.getString("bot.link").concat(startParameter);
        BigDecimal referralBalance = BigDecimal.valueOf(userService.getReferralBalanceByChatId(chatId));
        String currentBalance = BigDecimalUtil.roundToPlainString(referralBalance);
        List<ReferralUser> referralUsers = userService.getUserReferralsByChatId(chatId);
        String numberOfReferrals = String.valueOf(referralUsers.size());
        int numberOfActiveReferrals = (int) referralUsers.stream()
                .filter(usr -> dealRepository.getCountPassedByUserChatId(usr.getChatId()) > 0).count();

        Long dealsCount = dealRepository.getCountPassedByUserChatId(chatId);
        Rank rank = Rank.getByDealsNumber(dealsCount.intValue());
        String resultMessage;
        String referralMessageFewFiat = MessagePropertiesUtil.getMessage("referral.main.few.fiat");
        if (Objects.nonNull(referralMessageFewFiat)) {
            String refBalanceString = BotProperties.VARIABLE.isNotBlank("course.rub.byn")
                    ? BigDecimalUtil.roundToPlainString(referralBalance.multiply(BotProperties.VARIABLE.getBigDecimal("course.rub.byn")), 2)
                    : BigDecimalUtil.roundToPlainString(referralBalance);
            resultMessage = String.format(referralMessageFewFiat,
                    refLink, currentBalance, refBalanceString,
                    numberOfReferrals, numberOfActiveReferrals,
                    userService.getChargesByChatId(chatId), dealsCount, rank.getSmile(), rank.getPercent()).concat("%");;
        } else {
            resultMessage = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.REFERRAL_MAIN),
                    refLink, currentBalance, numberOfReferrals, numberOfActiveReferrals,
                    userService.getChargesByChatId(chatId), dealsCount, rank.getSmile(), rank.getPercent()).concat("%");
        }

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
