package tgb.btc.rce.service.processors.referral;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.ReferralUser;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.Rank;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@CommandProcessor(command = Command.REFERRAL)
public class Referral extends Processor {

    private IDealCountService dealCountService;

    private IBigDecimalService bigDecimalService;

    @Autowired
    public void setBigDecimalService(IBigDecimalService bigDecimalService) {
        this.bigDecimalService = bigDecimalService;
    }

    @Autowired
    public void setDealCountService(IDealCountService dealCountService) {
        this.dealCountService = dealCountService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        String startParameter = "?start=" + chatId;
        String refLink = PropertiesPath.BOT_PROPERTIES.getString("bot.link").concat(startParameter);
        BigDecimal referralBalance = BigDecimal.valueOf(readUserService.getReferralBalanceByChatId(chatId));
        String currentBalance = bigDecimalService.roundToPlainString(referralBalance);
        List<ReferralUser> referralUsers = readUserService.getUserReferralsByChatId(chatId);
        String numberOfReferrals = String.valueOf(referralUsers.size());
        int numberOfActiveReferrals = (int) referralUsers.stream()
                .filter(usr -> dealCountService.getCountPassedByUserChatId(usr.getChatId()) > 0).count();

        Long dealsCount = dealCountService.getCountPassedByUserChatId(chatId);
        Rank rank = Rank.getByDealsNumber(dealsCount.intValue());
        String resultMessage;
        String referralMessageFewFiat = messagePropertiesService.getMessage("referral.main.few.fiat");
        if (Objects.nonNull(referralMessageFewFiat)) {
            String refBalanceString = PropertiesPath.VARIABLE_PROPERTIES.isNotBlank("course.rub.byn")
                    ? bigDecimalService.roundToPlainString(referralBalance.multiply(PropertiesPath.VARIABLE_PROPERTIES.getBigDecimal("course.rub.byn")), 2)
                    : bigDecimalService.roundToPlainString(referralBalance);
            resultMessage = String.format(referralMessageFewFiat,
                    refLink, currentBalance, refBalanceString,
                    numberOfReferrals, numberOfActiveReferrals,
                    readUserService.getChargesByChatId(chatId), dealsCount, rank.getSmile(), rank.getPercent()).concat("%");
        } else {
            resultMessage = String.format(messagePropertiesService.getMessage(PropertiesMessage.REFERRAL_MAIN),
                    refLink, currentBalance, numberOfReferrals, numberOfActiveReferrals,
                    readUserService.getChargesByChatId(chatId), dealsCount, rank.getSmile(), rank.getPercent()).concat("%");
        }

        responseSender.sendMessage(chatId, resultMessage, keyboardBuildService.buildInline(getButtons(refLink)));
    }

    private List<InlineButton> getButtons(String refLink) {
        return List.of(InlineButton.builder()
                        .text("Пригласить друга")
                        .data(refLink)
                        .inlineType(InlineType.SWITCH_INLINE_QUERY)
                        .build(),
                InlineButton.builder()
                        .text(Command.WITHDRAWAL_OF_FUNDS.getText())
                        .data(Command.WITHDRAWAL_OF_FUNDS.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build());
    }
}
