package tgb.btc.rce.service.processors.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentRequisite;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.PaymentRequisiteRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.ReplyButton;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ExchangeService {

    public static final String USE_PROMO = "use_promo";
    public static final String DONT_USE_PROMO = "dont_use_promo";

    public static final String USE_REFERRAL_DISCOUNT = "use_discount";
    public static final String DONT_USE_REFERRAL_DISCOUNT = "dont_use_discount";

    public static final String USE_SAVED_WALLET = "use_saved";

    private final ResponseSender responseSender;

    private final UserService userService;

    private final DealService dealService;

    private final PaymentConfigService paymentConfigService;

    private final BotMessageService botMessageService;

    private UserDiscountRepository userDiscountRepository;

    private PaymentTypeRepository paymentTypeRepository;

    private PaymentRequisiteRepository paymentRequisiteRepository;

    private DealRepository dealRepository;

    private PaymentRequisiteService paymentRequisiteService;

    private ExchangeServiceNew exchangeServiceNew;

    private CalculateService calculateService;

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setExchangeServiceNew(ExchangeServiceNew exchangeServiceNew) {
        this.exchangeServiceNew = exchangeServiceNew;
    }

    @Autowired
    public void setPaymentRequisiteService(PaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    private static Map<Long, BigDecimal> USERS_PERSONAL_BUY = new HashMap<>();

    public static void putToUsersPersonalBuy(Long userChatId, BigDecimal personalBuy) {
        if (Objects.isNull(personalBuy)) {
            throw new BaseException("Персональная скидка на покупку не может быть null.");
        }
        USERS_PERSONAL_BUY.put(userChatId, personalBuy);
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public ExchangeService(ResponseSender responseSender, UserService userService, DealService dealService,
                           PaymentConfigService paymentConfigService, BotMessageService botMessageService) {
        this.responseSender = responseSender;
        this.userService = userService;
        this.dealService = dealService;
        this.paymentConfigService = paymentConfigService;
        this.botMessageService = botMessageService;
    }

    public Boolean savePaymentType(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }
        responseSender.deleteMessage(UpdateUtil.getChatId(update),
                update.getCallbackQuery().getMessage().getMessageId());
        PaymentType paymentType = paymentTypeRepository.getByPid(Long.parseLong(update.getCallbackQuery().getData()));
        Long currentDealPid = userService.getCurrentDealByChatId(UpdateUtil.getChatId(update));
        if (paymentType.getMinSum().compareTo(dealRepository.getAmountByPid(currentDealPid)) > 0) {
            Long chatId = UpdateUtil.getChatId(update);
            responseSender.sendMessage(chatId, "Минимальная сумма для покупки через "
                    + paymentType.getName() + " равна " + paymentType.getMinSum().toPlainString());
            userService.previousStep(chatId);
            userService.previousStep(chatId);
            userService.previousStep(chatId);
            currentDealPid = userService.getCurrentDealByChatId(chatId);
            dealRepository.updateIsPersonalAppliedByPid(currentDealPid, false);
            exchangeServiceNew.askForSum(chatId, dealRepository.getFiatCurrencyByPid(currentDealPid),
                    dealService.getCryptoCurrencyByPid(currentDealPid), DealType.BUY);
            return null;
        }
        dealService.updatePaymentTypeByPid(paymentType, currentDealPid);
        return true;
    }

    public void buildDeal(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
        Rank rank = Rank.getByDealsNumber(dealService.getCountPassedByUserChatId(chatId).intValue());
        boolean isRankDiscountOn = BooleanUtils.isTrue(
                BotVariablePropertiesUtil.getBoolean(BotVariableType.DEAL_RANK_DISCOUNT_ENABLE))
                && BooleanUtils.isNotFalse(userDiscountRepository.getRankDiscountByUserChatId(chatId));
        if (!Rank.FIRST.equals(rank) && isRankDiscountOn) {
            BigDecimal commission = deal.getCommission();
            BigDecimal rankDiscount = BigDecimalUtil.multiplyHalfUp(commission, calculateService.getPercentsFactor(
                    BigDecimal.valueOf(rank.getPercent())));
            deal.setAmount(BigDecimalUtil.subtractHalfUp(deal.getAmount(), rankDiscount));
        }
        CryptoCurrency currency = deal.getCryptoCurrency();
        PaymentType paymentType = deal.getPaymentType();
        String requisites;
        List<PaymentRequisite> paymentRequisite = paymentRequisiteRepository.getByPaymentTypePid(paymentType.getPid());
        if (CollectionUtils.isEmpty(paymentRequisite)) {
            throw new BaseException("Не установлены реквизиты для " + paymentType.getName() + ".");
        }
        if (BooleanUtils.isNotTrue(deal.getPaymentType().getDynamicOn()) || paymentRequisite.size() == 1) {
            requisites = paymentRequisite.get(0).getRequisite();
        } else if (paymentRequisite.size() > 0){
            Integer order = paymentRequisiteService.getOrder(paymentType.getPid());
            requisites = paymentRequisiteRepository.getRequisiteByPaymentTypePidAndOrder(paymentType.getPid(), order);
        } else throw new BaseException("Не найдены реквизиты для " + paymentType.getName() + ".");

        String promoCodeText = Boolean.TRUE.equals(deal.getUsedPromo())
                ?
                "\n\n<b> Использован скидочный промокод</b>: "
                        + BotVariablePropertiesUtil.getVariable(BotVariableType.PROMO_CODE_NAME) + "\n\n"
                : "\n\n";

        BigDecimal dealAmount = deal.getAmount();

        if (BooleanUtils.isTrue(deal.getUsedReferralDiscount())) {
            Integer referralBalance = userService.getReferralBalanceByChatId(UpdateUtil.getChatId(update));
            deal.setOriginalPrice(deal.getAmount());
            if (referralBalance <= deal.getAmount().intValue()) {
                dealAmount = deal.getAmount().subtract(BigDecimal.valueOf(referralBalance));
            } else {
                dealAmount = BigDecimal.ZERO;
            }
        }
        deal.setAmount(dealAmount);
        deal = dealService.save(deal);

        String message = "✅<b>Заявка №</b><code>" + deal.getPid() + "</code> успешно создана."
                + "\n\n"
                + "<b>Получаете</b>: "
                + BigDecimalUtil.round(deal.getCryptoAmount(), currency.getScale()).stripTrailingZeros()
                .toPlainString() + " " + currency.getShortName()
                + "\n"
                + "<b>" + deal.getCryptoCurrency()
                .getDisplayName() + "-адрес</b>:" + "<code>" + deal.getWallet() + "</code>"
                + "\n\n"
                + "Ваш ранг: " + rank.getSmile() + ", скидка " + rank.getPercent() + "%" + "\n\n"
                + "<b>\uD83D\uDCB5Сумма к оплате</b>: <code>" + BigDecimalUtil.round(dealAmount, 0).stripTrailingZeros()
                .toPlainString() + " " + deal.getFiatCurrency().getDisplayName() + "</code>"
                + "\n"
                + "<b>Резквизиты для оплаты:</b>"
                + "\n\n"
                + "<code>" + requisites + "</code>"
                + "\n\n"
                + "<b>⏳Заявка действительна</b>: " + BotVariablePropertiesUtil.getVariable(
                BotVariableType.DEAL_ACTIVE_TIME) + " минут"
                + "\n\n"
                + "☑️После успешного перевода денег по указанным реквизитам нажмите на кнопку <b>\""
                + Command.PAID.getText() + "\"</b> или же вы можете отменить данную заявку, нажав на кнопку <b>\""
                + Command.CANCEL_DEAL.getText() + "\"</b>."
                + promoCodeText;

        ReplyKeyboard keyboard = KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text(Command.PAID.getText())
                        .data(Command.PAID.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text(Command.CANCEL.getText())
                        .data(Command.CANCEL_DEAL.name())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build()
        ));
        Optional<Message> optionalMessage = responseSender.sendMessage(chatId, message, keyboard, "HTML");
        deal.setDateTime(LocalDateTime.now());
        dealService.save(deal);
        DealDeleteScheduler.addNewCryptoDeal(deal.getPid(), optionalMessage.map(Message::getMessageId).orElse(null));
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
