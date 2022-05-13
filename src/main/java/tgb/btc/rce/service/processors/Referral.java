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
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
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

    private final WithdrawalRequestService withdrawalRequestService;

    @Autowired
    public Referral(IResponseSender responseSender, UserService userService,
                    WithdrawalRequestService withdrawalRequestService) {
        super(responseSender, userService);
        this.withdrawalRequestService = withdrawalRequestService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String startParameter = "?start=" + chatId;
        String refLink = BotPropertiesUtil.getProperty("bot.link").concat(startParameter);
        String currentBalance = userService.getReferralBalanceByChatId(chatId).toString();
        List<ReferralUser> referralUsers = userService.getUserReferralsByChatId(chatId);
        String numberOfReferrals = String.valueOf(referralUsers.size());
        String sumFromReferrals = getSumOfReferrals(referralUsers);
        Integer reserve = withdrawalRequestService.getCreatedTotalSumByChatId(chatId);
        if (StringUtils.hasLength(sumFromReferrals) && reserve > 0)
            sumFromReferrals = sumFromReferrals.concat("(в резерве " + reserve);

        String resultMessage = String.format(MessagePropertiesUtil.getMessage(PropertiesMessage.REFERRAL_MAIN),
                refLink, currentBalance, numberOfReferrals, sumFromReferrals);

        responseSender.sendMessage(chatId, resultMessage, KeyboardUtil.buildInlineDiff(getButtons(chatId)));
    }

    private List<InlineButton> getButtons(Long chatId) {
        return List.of(InlineButton.builder()
                        .text("Пригласить друга")
                        .data("?start=" + chatId)
                        .inlineType(InlineType.SWITCH_INLINE_QUERY)
                        .build(),
                InlineButton.builder()
                        .text("Вывод средств")
                        .data(Command.WITHDRAWAL_OF_FUNDS.getText())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build());
    }

    private String getSumOfReferrals(List<ReferralUser> referralUsers) {
        /* TODO Егор
            У класса ReferralUser есть поле sum. Надо посчитать сумму всех юзеров и вернуть результат в виде строки.
            List - почти тоже самое, что и массив, позже пройдем.
            Если бы referralUsers был бы массивом, то чтобы получить элемент по индексу 1 ты бы использовал referralUsers[1],
            а здесь используй referralUsers.get(1)
         */
        return null;
    }
}
