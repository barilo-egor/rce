package tgb.btc.rce.service.processors;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.ConverterUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;
import java.util.Objects;

@CommandProcessor(command = Command.CONFIRM_USER_DEAL)
public class ConfirmUserDeal extends Processor {

    private final DealService dealService;

    @Autowired
    public ConfirmUserDeal(IResponseSender responseSender, UserService userService, DealService dealService) {
        super(responseSender, userService);
        this.dealService = dealService;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Deal deal = dealService.getByPid(Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));

        deal.setActive(false);
        deal.setPassed(true);
        deal.setCurrent(false);
        dealService.save(deal);
        User user = deal.getUser();
        if (Objects.nonNull(user.getLotteryCount())) user.setLotteryCount(user.getLotteryCount() + 1);
        else user.setLotteryCount(1);
        userService.save(user);
        responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
        if (user.getFromChatId() != null) {
            BigDecimal sumToAdd = BigDecimalUtil.multiplyHalfUp(BigDecimal.valueOf(user.getReferralBalance()),
                    ConverterUtil.getPercentsFactor(
                            BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.REFERRAL_PERCENT))));
            userService.updateReferralBalanceByChatId(user.getReferralBalance() + sumToAdd.intValue(), user.getChatId());
            userService.updateChargesByChatId(user.getCharges() + sumToAdd.intValue(), user.getChatId());
        }
        switch (deal.getCryptoCurrency()) {
            case BITCOIN:
                responseSender.sendMessage(deal.getUser().getChatId(),
                        "Биткоин отправлен ✅\nhttps://www.blockchain.com/btc/address/" + deal.getWallet());
                break;
            case LITECOIN:
                responseSender.sendMessage(deal.getUser().getChatId(),
                        "Валюта отправлена.");
                break;
            case USDT:
                responseSender.sendMessage(deal.getUser().getChatId(),
                        "Валюта отправлена.");
                break;
        }
    }
}
