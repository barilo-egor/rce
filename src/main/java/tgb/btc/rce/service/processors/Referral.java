package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.ReferralUser;
import tgb.btc.rce.bean.WithdrawalRequest;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.enums.Rank;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.impl.WithdrawalRequestService;
import tgb.btc.rce.util.BotPropertiesUtil;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;

@CommandProcessor(command = Command.REFERRAL)
public class Referral extends Processor {

    private final DealService dealService;

    @Autowired
    public Referral(IResponseSender responseSender, UserService userService, DealService dealService) {
        super(responseSender, userService);
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String startParameter = "?start=" + chatId;
        String refLink = BotPropertiesUtil.getProperty("bot.link").concat(startParameter);
        String currentBalance = userService.getReferralBalanceByChatId(chatId).toString();
        List<ReferralUser> referralUsers = userService.getUserReferralsByChatId(chatId);
        String numberOfReferrals = String.valueOf(referralUsers.size());

        Long dealsCount = dealService.getCountPassedByUserChatId(chatId);
        Rank rank = Rank.getByDealsNumber(dealsCount.intValue());
        String resultMessage = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.REFERRAL_MAIN),
                refLink, currentBalance, numberOfReferrals, userService.getChargesByChatId(chatId), dealsCount, rank.getSmile(), rank.getPercent()).concat("%");

        responseSender.sendMessage(chatId, resultMessage, KeyboardUtil.buildInlineDiff(getButtons(refLink)));
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
