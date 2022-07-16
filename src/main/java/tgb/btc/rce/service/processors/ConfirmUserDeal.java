package tgb.btc.rce.service.processors;

import org.apache.commons.lang.BooleanUtils;
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
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.List;
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
        User user = deal.getUser();

        deal.setActive(false);
        deal.setPassed(true);
        deal.setCurrent(false);

        if (BooleanUtils.isTrue(deal.getUsedReferralDiscount())) {
            Integer referralBalance = userService.getReferralBalanceByChatId(UpdateUtil.getChatId(update));

            BigDecimal sumWithDiscount;
            if (referralBalance <= deal.getAmount().intValue()) {
                sumWithDiscount = deal.getAmount().subtract(BigDecimal.valueOf(referralBalance));
                referralBalance = BigDecimal.ZERO.intValue();
            } else {
                sumWithDiscount = BigDecimal.ZERO;
                referralBalance = referralBalance - deal.getAmount().intValue();
            }

            user.setReferralBalance(referralBalance);
            deal.setAmount(sumWithDiscount);
        }
        dealService.save(deal);
        if (Objects.nonNull(user.getLotteryCount())) user.setLotteryCount(user.getLotteryCount() + 1);
        else user.setLotteryCount(1);
        userService.save(user);
        responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
        if (user.getFromChatId() != null) {
            User refUser = userService.findByChatId(user.getFromChatId());
            BigDecimal sumToAdd = BigDecimalUtil.multiplyHalfUp(deal.getAmount(),
                    ConverterUtil.getPercentsFactor(
                            BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.REFERRAL_PERCENT))));
            userService.updateReferralBalanceByChatId(refUser.getReferralBalance() + sumToAdd.intValue(), refUser.getChatId());
            userService.updateChargesByChatId(refUser.getCharges() + sumToAdd.intValue(), refUser.getChatId());
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

        Integer reviewPrise = BotVariablePropertiesUtil.getInt(BotVariableType.REVIEW_PRISE);
        responseSender.sendMessage(deal.getUser().getChatId(), "Хотите оставить отзыв?\n" +
                        "За оставленный отзыв вы получите вознагрождение в размере " + reviewPrise
                        + "₽ на реферальный баланс после публикации.",
                KeyboardUtil.buildInline(List.of(
                        InlineButton.builder()
                                .data(Command.SHARE_REVIEW.getText())
                                .text("Оставить")
                                .build()
                        )
                ));
    }
}
