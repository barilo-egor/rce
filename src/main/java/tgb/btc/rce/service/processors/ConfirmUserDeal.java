package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
            Integer referralBalance = user.getReferralBalance();

            log.info("Снятие с реф баланса в учет скидки. chatId = " + user.getChatId() + ", referralBalance = "
                    + referralBalance);
            BigDecimal sumWithDiscount;
            if (referralBalance <= deal.getAmount().intValue()) {
                sumWithDiscount = deal.getAmount().subtract(BigDecimal.valueOf(referralBalance));
                referralBalance = BigDecimal.ZERO.intValue();
            } else {
                sumWithDiscount = BigDecimal.ZERO;
                referralBalance = referralBalance - deal.getAmount().intValue();
            }
            log.info("Подсчет total после использования скидки с реф баланса = " + referralBalance +
                    ", сумма устанавливается юзеру чат айди = " + user.getChatId());
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
            Integer total = refUser.getReferralBalance() + sumToAdd.intValue();
            log.info("Подтверждение сделки, зачисление на реф баланс пользователю. Админ чат айди = "
                    + UpdateUtil.getChatId(update) + ". refUserChatId = " + refUser.getChatId() + ", sumToAdd = "
                    + sumToAdd.toPlainString() + ", refUser.referralBalance = " + refUser.getReferralBalance().toString()
                    + ", total = " + total);
            userService.updateReferralBalanceByChatId(total, refUser.getChatId());
            userService.updateChargesByChatId(refUser.getCharges() + sumToAdd.intValue(), refUser.getChatId());
        }
        switch (deal.getCryptoCurrency()) {
            case BITCOIN:
                switch (deal.getDealType()) {
                    case BUY:
                        responseSender.sendMessage(deal.getUser().getChatId(),
                                "Биткоин отправлен ✅\nhttps://www.blockchain.com/btc/address/" + deal.getWallet());
                        break;
                    case SELL:
                        responseSender.sendMessage(deal.getUser().getChatId(),
                                "Заявка обработана, деньги отправлены.");
                        break;
                }
                break;
            case LITECOIN:
                switch (deal.getDealType()) {
                    case BUY:
                        responseSender.sendMessage(deal.getUser().getChatId(),
                                "Валюта отправлена.\nhttps://blockchair.com/ru/litecoin/address/" + deal.getWallet());
                        break;
                    case SELL:
                        responseSender.sendMessage(deal.getUser().getChatId(),
                                "Заявка обработана, деньги отправлены.");
                        break;
                }
            case USDT:
                switch (deal.getDealType()) {
                    case BUY:
                        responseSender.sendMessage(deal.getUser().getChatId(),
                                "Валюта отправлена.https://tronscan.io/#/address/" + deal.getWallet());
                        break;
                    case SELL:
                        responseSender.sendMessage(deal.getUser().getChatId(),
                                "Заявка обработана, деньги отправлены.");
                        break;
                }
        }

        Integer reviewPrise = BotVariablePropertiesUtil.getInt(BotVariableType.REVIEW_PRISE);
        responseSender.sendMessage(deal.getUser().getChatId(), "Хотите оставить отзыв?\n" +
                        "За оставленный отзыв вы получите вознагрождение в размере до 30₽" +
                        " на реферальный баланс после публикации.",
                KeyboardUtil.buildInline(List.of(
                        InlineButton.builder()
                                .data(Command.SHARE_REVIEW.getText())
                                .text("Оставить")
                                .build()
                        )
                ));
    }
}
