package tgb.btc.rce.service.processors.support;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.InlineType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class ExchangeService {

    public static final String USE_REFERRAL_DISCOUNT = "use_discount";
    public static final String DONT_USE_REFERRAL_DISCOUNT = "dont_use_discount";

    private final ResponseSender responseSender;

    private final UserService userService;

    private final DealService dealService;

    @Autowired
    public ExchangeService(ResponseSender responseSender, UserService userService, DealService dealService) {
        this.responseSender = responseSender;
        this.userService = userService;
        this.dealService = dealService;
    }

    public void confirmDeal(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        dealService.updateIsActiveByPid(true, currentDealPid);
        userService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_CONFIRMED));
        userService.getAdminsChatIds().forEach(adminChatId ->
                responseSender.sendMessage(adminChatId,
                        "Поступила новая заявка на покупку.",
                        KeyboardUtil.buildInline(List.of(
                                InlineButton.builder()
                                        .text(Command.SHOW_DEAL.getText())
                                        .data(Command.SHOW_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                                + currentDealPid)
                                        .build()
                        ))));
        userService.updateCurrentDealByChatId(null, chatId);
    }

    public void askForReferralDiscount(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.findById(userService.getCurrentDealByChatId(chatId));
        Integer referralBalance = userService.getReferralBalanceByChatId(chatId);

        String message = "\uD83E\uDD11У вас есть " + referralBalance + "₽ на реферальном балансе. Использовать их в качестве скидки?";

        BigDecimal sumWithDiscount;
        if (referralBalance <= deal.getAmount().intValue()) {
            sumWithDiscount = deal.getAmount().subtract(BigDecimal.valueOf(referralBalance));
        } else {
            sumWithDiscount = BigDecimal.ZERO;
        }

        ReplyKeyboard keyboard = KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("Со скидкой, " + sumWithDiscount.stripTrailingZeros().toPlainString())
                        .data(USE_REFERRAL_DISCOUNT)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Без скидки, " + deal.getAmount().stripTrailingZeros().toPlainString())
                        .data(DONT_USE_REFERRAL_DISCOUNT)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON
        ));

        responseSender.sendMessage(chatId, message, keyboard, "HTML");
    }

    public void processReferralDiscount(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        dealService.updateUsedReferralDiscountByPid(true, userService.getCurrentDealByChatId(chatId));
    }

    public void askForReceipts(Update update) {
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Отправьте скрин перевода, либо чек оплаты..",
                KeyboardUtil.buildReply(
                        List.of(ReplyButton.builder().text(Command.RECEIPTS_CANCEL_DEAL.getText())
                                .build())));
    }

}
