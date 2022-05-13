package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.WITHDRAWAL_OF_FUNDS)
public class WithdrawalOfFunds extends Processor {

    private final UserService userService;

    @Autowired
    public WithdrawalOfFunds(IResponseSender responseSender, UserService userService) {
        super(responseSender);
        this.userService = userService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (userService.getReferralBalanceByChatId(chatId) <
                BotVariablePropertiesUtil.getInt(BotVariableType.MIN_WITHDRAWAL_OF_FUNDS_SUM)) {
            responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.REFERRAL_MIN_SUM));
            return;
        }
    }
}
