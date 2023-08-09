package tgb.btc.rce.service.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.User;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.CalculateService;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.PaymentRequisiteService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@CommandProcessor(command = Command.CONFIRM_USER_DEAL)
@Slf4j
public class ConfirmUserDeal extends Processor {

    private DealService dealService;

    private PaymentRequisiteService paymentRequisiteService;

    private CalculateService calculateService;

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setPaymentRequisiteService(PaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Override
    @Transactional
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        Deal deal = dealService.getByPid(Long.parseLong(
                update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER)[1]));
        User user = deal.getUser();

        deal.setActive(false);
        deal.setPassed(true);

        if (BooleanUtils.isTrue(deal.getUsedReferralDiscount())) {
            BigDecimal referralBalance = BigDecimal.valueOf(user.getReferralBalance());
            BigDecimal sumWithDiscount;
            if (ReferralType.STANDARD.isCurrent() && FiatCurrency.BYN.equals(deal.getFiatCurrency())) {
                referralBalance = referralBalance.multiply(BotProperties.BOT_VARIABLE.getBigDecimal("course.rub.byn"));
            }
            if (referralBalance.compareTo(deal.getOriginalPrice()) < 1) {
                sumWithDiscount = deal.getOriginalPrice().subtract(referralBalance);
                referralBalance = BigDecimal.ZERO;
            } else {
                sumWithDiscount = BigDecimal.ZERO;
                referralBalance = referralBalance.subtract(deal.getOriginalPrice()).setScale(0, RoundingMode.HALF_UP);
                if (ReferralType.STANDARD.isCurrent() && FiatCurrency.BYN.equals(deal.getFiatCurrency())) {
                    referralBalance = referralBalance.divide(BotProperties.BOT_VARIABLE.getBigDecimal("course.byn.rub"), RoundingMode.HALF_UP);
                }
            }
            user.setReferralBalance(referralBalance.intValue());
            deal.setAmount(sumWithDiscount);
        }
        dealService.save(deal);
        DealDeleteScheduler.deleteCryptoDeal(deal.getPid());
        paymentRequisiteService.updateOrder(deal.getPaymentType().getPid());
        if (Objects.nonNull(user.getLotteryCount())) user.setLotteryCount(user.getLotteryCount() + 1);
        else user.setLotteryCount(1);
        user.setCurrentDeal(null);
        userService.save(user);
        responseSender.deleteMessage(UpdateUtil.getChatId(update), UpdateUtil.getMessage(update).getMessageId());
        if (user.getFromChatId() != null) {
            User refUser = userService.findByChatId(user.getFromChatId());
            BigDecimal refUserReferralPercent = userRepository.getReferralPercentByChatId(refUser.getChatId());
            boolean isGeneralReferralPercent = Objects.isNull(refUserReferralPercent) || refUserReferralPercent.compareTo(BigDecimal.ZERO) == 0;
            BigDecimal referralPercent = isGeneralReferralPercent
                    ? BigDecimal.valueOf(BotVariablePropertiesUtil.getDouble(BotVariableType.REFERRAL_PERCENT))
                    : refUserReferralPercent;
            BigDecimal sumToAdd = BigDecimalUtil.multiplyHalfUp(deal.getAmount(),
                    calculateService.getPercentsFactor(referralPercent));
            if (ReferralType.STANDARD.isCurrent() && FiatCurrency.BYN.equals(deal.getFiatCurrency())) {
                sumToAdd = sumToAdd.divide(BotProperties.BOT_VARIABLE.getBigDecimal("course.byn.rub"), RoundingMode.HALF_UP);
            }
            Integer total = refUser.getReferralBalance() + sumToAdd.intValue();
            userService.updateReferralBalanceByChatId(total, refUser.getChatId());
            if (BigDecimal.ZERO.compareTo(sumToAdd) != 0)
                responseSender.sendMessage(refUser.getChatId(), "На реферальный баланс было добавлено " + sumToAdd.intValue() + "₽ по сделке партнера.");
            userService.updateChargesByChatId(refUser.getCharges() + sumToAdd.intValue(), refUser.getChatId());
        }
        String message;
        if (!DealType.isBuy(deal.getDealType())) {
            message = "Заявка обработана, деньги отправлены.";
        } else {
            switch (deal.getCryptoCurrency()) {
                case BITCOIN:
                    message = "Биткоин отправлен ✅\nhttps://blockchair.com/bitcoin/address/" + deal.getWallet();
                    break;
                case LITECOIN:
                    message = "Валюта отправлена.\nhttps://blockchair.com/ru/litecoin/address/" + deal.getWallet();
                    break;
                case USDT:
                    message = "Валюта отправлена.https://tronscan.io/#/address/" + deal.getWallet();
                    break;
                case MONERO:
                    message = "Валюта отправлена."; // TODO добавить url
                    break;
                default:
                    throw new BaseException("Не найдена криптовалюта у сделки. dealPid=" + deal.getPid());
            }
        }
        responseSender.sendMessage(deal.getUser().getChatId(), message);

        if (UserService.REFERRAL_TYPE.equals(ReferralType.STANDARD)) {
            Integer reviewPrise = BotVariablePropertiesUtil.getInt(BotVariableType.REVIEW_PRISE); // TODO уточнить нужно ли 30 выводить или брать число из проперти
            responseSender.sendMessage(deal.getUser().getChatId(), "Хотите оставить отзыв?\n" +
                            "За оставленный отзыв вы получите вознаграждение в размере до 30₽" +
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
}
