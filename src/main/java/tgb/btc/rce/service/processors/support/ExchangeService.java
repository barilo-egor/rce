package tgb.btc.rce.service.processors.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentRequisite;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.exception.NumberParseException;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    public boolean saveSum(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
        Double sum = UpdateUtil.getDoubleFromText(update);
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        Double minSum = BotVariablePropertiesUtil.getMinSumBuy(cryptoCurrency);

        if (sum < minSum) {
            responseSender.sendMessage(chatId, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + BigDecimal.valueOf(minSum).stripTrailingZeros().toPlainString() + ".");
            return false;
        }

        deal.setCryptoAmount(BigDecimal.valueOf(sum));
        BigDecimal amount = CalculateUtil.convertCryptoToRub(cryptoCurrency, sum, DealType.BUY);
        BigDecimal personalBuy = USERS_PERSONAL_BUY.get(chatId);
        if (BooleanUtils.isNotTrue(deal.getPersonalApplied())) {
            if (Objects.isNull(personalBuy)) {
                personalBuy = userDiscountRepository.getPersonalBuyByChatId(chatId);
                if (Objects.nonNull(personalBuy) && !BigDecimal.ZERO.equals(personalBuy)) {
                    amount = amount.add(CalculateUtil.getPercentsFactor(amount).multiply(personalBuy));
                    deal.setPersonalApplied(true);
                }
                if (Objects.nonNull(personalBuy)) {
                    putToUsersPersonalBuy(chatId, personalBuy);
                } else {
                    putToUsersPersonalBuy(chatId, BigDecimal.ZERO);
                }
            } else if (!BigDecimal.ZERO.equals(personalBuy)) {
                amount = amount.add(CalculateUtil.getPercentsFactor(amount).multiply(personalBuy));
                deal.setPersonalApplied(true);
            }
        }
        BigDecimal bulkDiscount = BulkDiscountUtil.getPercentBySum(amount);
        if (!BigDecimal.ZERO.equals(bulkDiscount)) {
            amount = amount.subtract(CalculateUtil.getPercentsFactor(amount).multiply(bulkDiscount));
        }
        deal.setAmount(amount);
        deal.setCommission(CalculateUtil.getCommission(BigDecimal.valueOf(sum), cryptoCurrency, DealType.BUY));
        dealService.save(deal);
        return true;
    }

    public void convertToRub(Update update, Long currentDealPid) {
        Long chatId = UpdateUtil.getChatId(update);
        System.out.println();
        String query = update.getInlineQuery().getQuery().replaceAll(",", ".");
        BigDecimal sum;
        CryptoCurrency currency = null;

        if (!hasInputSum(query)) {
            askForCryptoSum(update);
            return;
        }

        try {
            currency = CryptoCurrency.fromShortName(query.substring(0, query.indexOf("-")));
            sum = BigDecimal.valueOf(NumberUtil.getInputDouble(query.substring(query.indexOf(" ") + 1)));
        } catch (EnumTypeNotFoundException e) {
            askForCryptoSum(update);
            return;
        } catch (NumberParseException e) {
            if (hasInputSum(currency, query)) {
                sendInlineAnswer(update, e.getMessage(), false);
            } else {
                askForCryptoSum(update);
            }
            return;
        }

        CryptoCurrency cryptoCurrency = dealService.getCryptoCurrencyByPid(currentDealPid);
        double minSum = BigDecimalUtil.round(BotVariablePropertiesUtil.getMinSumBuy(cryptoCurrency),
                        cryptoCurrency.getScale())
                .doubleValue();

        if (sum.doubleValue() < minSum) {
            sendInlineAnswer(update, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + BigDecimal.valueOf(minSum).stripTrailingZeros().toPlainString() + ".", false);
            return;
        }
        sum = BigDecimalUtil.round(sum, cryptoCurrency.getScale());
        BigDecimal roundedConvertedSum = BigDecimalUtil.round(
                CalculateUtil.convertCryptoToRub(currency, sum.doubleValue(), DealType.BUY), 0);
        BigDecimal personalBuy = USERS_PERSONAL_BUY.get(chatId);
        if (Objects.isNull(personalBuy) || !BigDecimal.ZERO.equals(personalBuy)) {
            personalBuy = userDiscountRepository.getPersonalBuyByChatId(chatId);
            if (Objects.nonNull(personalBuy) && !BigDecimal.ZERO.equals(personalBuy)) {
                if (BigDecimal.ZERO.compareTo(personalBuy) > 0) {
                    roundedConvertedSum = roundedConvertedSum.add(CalculateUtil.getPercentsFactor(roundedConvertedSum).multiply(personalBuy));
                } else {
                    roundedConvertedSum = roundedConvertedSum.add(CalculateUtil.getPercentsFactor(roundedConvertedSum).multiply(personalBuy));
                }
            }
            if (Objects.nonNull(personalBuy)) {
                putToUsersPersonalBuy(chatId, personalBuy);
            } else {
                putToUsersPersonalBuy(chatId, BigDecimal.ZERO);
            }
        }
        BigDecimal bulkDiscount = BulkDiscountUtil.getPercentBySum(roundedConvertedSum);
        if (!BigDecimal.ZERO.equals(bulkDiscount)) {
            roundedConvertedSum = roundedConvertedSum.subtract(CalculateUtil.getPercentsFactor(roundedConvertedSum).multiply(personalBuy));
        }
        String dealType = DealType.BUY.equals(dealService.getDealTypeByPid(currentDealPid))
                ? "Покупка: "
                : "Продажа: ";
        sendInlineAnswer(update,
                dealType + sum.stripTrailingZeros().toPlainString() + " " + currency.getDisplayName() + " ~ " +
                        roundedConvertedSum.stripTrailingZeros().toPlainString(), true);
    }

    private boolean hasInputSum(CryptoCurrency currency, String query) {
        return currency != null && query.length() > currency.getShortName().length() + 1;
    }

    private boolean hasInputSum(String query) {
        return query.contains(" ");
    }

    private void askForCryptoSum(Update update) {
        sendInlineAnswer(update, BotStringConstants.ENTER_CRYPTO_SUM, false);
    }

    private void sendInlineAnswer(Update update, String answer, boolean textPushButton) {
        String text = textPushButton
                ? "Нажмите сюда, чтобы отправить сумму"
                : "Введите сумму в криптовалюте.";
        String sum = update.getInlineQuery().getQuery().contains(" ")
                ?
                update.getInlineQuery().getQuery().substring(update.getInlineQuery().getQuery().indexOf(" "))
                : "Ошибка";
        responseSender.execute(AnswerInlineQuery.builder().inlineQueryId(update.getInlineQuery().getId())
                .result(InlineQueryResultArticle.builder()
                        .id(update.getInlineQuery().getId())
                        .title(answer)
                        .inputMessageContent(InputTextMessageContent.builder()
                                .messageText(sum)
                                .build())
                        .description(text)
                        .build())
                .build());
    }

    public void askForUserPromoCode(Long chatId, boolean back) {
        Deal deal = dealService.findById(userService.getCurrentDealByChatId(chatId));
        double dealCryptoAmount = deal.getCryptoAmount().setScale(deal.getCryptoCurrency().getScale(),
                        RoundingMode.HALF_UP).stripTrailingZeros()
                .doubleValue();

        double dealAmount;
        BigDecimal discount = BigDecimal.valueOf(
                        BotVariablePropertiesUtil.getDouble(BotVariableType.PROMO_CODE_DISCOUNT))
                .setScale(0, RoundingMode.HALF_UP);

        BigDecimal sumWithDiscount;

        if (back) {
            sumWithDiscount = deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros();
            dealAmount = sumWithDiscount.add(dealService.getDiscountByPid(deal.getPid())).doubleValue();
        } else {
            dealAmount = deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros().doubleValue();
            sumWithDiscount = deal.getAmount().subtract(BigDecimalUtil.multiplyHalfUp(deal.getCommission(),
                            CalculateUtil.getPercentsFactor(
                                    discount)))
                    .setScale(0, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
        }

        String message = "<b>Покупка " + deal.getCryptoCurrency().getDisplayName() + "</b>: " + dealCryptoAmount
                + "\n\n"
                + "<b>Сумма перевода</b>: <s>" + BigDecimal.valueOf(dealAmount).stripTrailingZeros()
                .toPlainString() + "</s> " + sumWithDiscount.stripTrailingZeros().toPlainString()
                + "\n\n"
                + "\uD83C\uDFAB У вас есть промокод: <b>" + BotVariablePropertiesUtil.getVariable(
                BotVariableType.PROMO_CODE_NAME)
                + "</b>, который даёт скидку в размере " + discount.stripTrailingZeros()
                .toPlainString() + "% от комиссии"
                + "\n\n"
                + "✅ <b>Использовать промокод</b> как скидку?";
        ReplyKeyboard keyboard = KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("Использовать, " + sumWithDiscount.stripTrailingZeros().toPlainString())
                        .data(USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Без промокода, " + dealAmount)
                        .data(DONT_USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON
        ));

        responseSender.sendMessage(chatId, message, keyboard, "HTML");
    }

    public void askForWallet(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        try {
            responseSender.deleteMessage(chatId, UpdateUtil.getMessage(update).getMessageId());
        } catch (Exception ignored) {
        }
        Deal deal = dealService.findById(userService.getCurrentDealByChatId(chatId));
        String message = "\uD83D\uDCDDВведите " + deal.getCryptoCurrency()
                .getDisplayName() + "-адрес кошелька, куда вы "
                + "хотите отправить " + BigDecimalUtil.round(deal.getCryptoAmount(),
                deal.getCryptoCurrency().getScale()).toPlainString()
                + " " + deal.getCryptoCurrency().getShortName();
        List<InlineButton> buttons = new ArrayList<>();

        if (dealService.getNotCurrentDealsCountByUserChatId(chatId, deal.getDealType()) > 0) {
            String wallet = dealService.getWalletFromLastNotCurrentByChatId(chatId, deal.getDealType());
            message = message.concat("\n\nВы можете использовать ваш сохраненный адрес:\n" + wallet);
            buttons.add(InlineButton.builder()
                    .text("Использовать сохраненный адрес")
                    .data(USE_SAVED_WALLET)
                    .build());
        }
        buttons.add(KeyboardUtil.INLINE_BACK_BUTTON);

        Optional<Message> optionalMessage = responseSender.sendMessage(chatId, message,
                KeyboardUtil.buildInline(buttons), "HTML");
        optionalMessage.ifPresent(
                sentMessage -> userService.updateBufferVariable(chatId, sentMessage.getMessageId().toString()));
    }

    public void processPromoCode(Update update) {
        if (!update.hasCallbackQuery()) {
            return;
        }
        Long chatId = UpdateUtil.getChatId(update);
        if (USE_PROMO.equals(update.getCallbackQuery().getData())) {
            Long currentDealPid = userService.getCurrentDealByChatId(chatId);
            BigDecimal discount = BigDecimal.valueOf(
                    BotVariablePropertiesUtil.getDouble(BotVariableType.PROMO_CODE_DISCOUNT));
            BigDecimal dealAmount = dealService.getAmountByPid(currentDealPid);
            BigDecimal totalDiscount = BigDecimalUtil.multiplyHalfUp(dealService.getCommissionByPid(currentDealPid),
                    CalculateUtil.getPercentsFactor(discount));
            dealService.updateDiscountByPid(totalDiscount, currentDealPid);
            BigDecimal sumWithDiscount = dealAmount.subtract(totalDiscount)
                    .setScale(0, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            dealService.updateAmountByPid(sumWithDiscount, currentDealPid);
            dealService.updateIsUsedPromoByPid(true, currentDealPid);
        }
    }

    public void saveWallet(Update update) {
        Long currentDealPid = userService.getCurrentDealByChatId(UpdateUtil.getChatId(update));
        if (update.hasMessage() && update.getMessage().hasText()) {
            String wallet = UpdateUtil.getMessageText(update);
            validateWallet(wallet);
            dealService.updateWalletByPid(wallet, currentDealPid);
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals(USE_SAVED_WALLET)) {
            dealService.updateWalletByPid(dealService.getWalletFromLastNotCurrentByChatId(UpdateUtil.getChatId(update),
                            dealService.getDealTypeByPid(
                                    currentDealPid)),
                    currentDealPid);
        }
    }

    private void validateWallet(String wallet) {
        if (wallet.length() < 33) {
            throw new BaseException("Длина кошелька не может быть меньше 33-х символов.");
        }
    }

    public void askForPaymentType(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
        BigDecimal dealCryptoAmount = deal.getCryptoAmount().setScale(deal.getCryptoCurrency().getScale(),
                RoundingMode.HALF_UP).stripTrailingZeros();
        BigDecimal dealAmount = deal.getAmount().setScale(0, RoundingMode.HALF_UP).stripTrailingZeros();
        String displayCurrencyName = deal.getCryptoCurrency().getDisplayName();
        String additionalText;
        try {
            additionalText = botMessageService.findByTypeThrows(BotMessageType.ADDITIONAL_DEAL_TEXT).getText() + "\n\n";
        } catch (BaseException e) {
            additionalText = StringUtils.EMPTY;
        }
        if (BooleanUtils.isTrue(deal.getUsedReferralDiscount())) {
            Integer referralBalance = userService.getReferralBalanceByChatId(UpdateUtil.getChatId(update));
            deal.setOriginalPrice(deal.getAmount());
            if (referralBalance <= deal.getAmount().intValue()) {
                dealAmount = deal.getAmount().subtract(BigDecimal.valueOf(referralBalance));
            } else {
                dealAmount = BigDecimal.ZERO;
            }
        }
        String message = "<b>" +
                "" +
                "\uD83D\uDCACИнформация по заявке</b>\n"
                + "<b>Покупка " + displayCurrencyName + "</b>: " + dealCryptoAmount.stripTrailingZeros()
                .toPlainString() + "\n"
                + "<b>" + displayCurrencyName + "-адрес</b>:" + "<code>" + deal.getWallet() + "</code>"
                + "\n\n"
                + "\uD83D\uDCB5<b>Сумма перевода</b>: " + dealAmount.stripTrailingZeros().toPlainString() + "₽"
                + "\n\n"
                + additionalText
                + "<b>Выберите способ оплаты:</b>";

        List<InlineButton> buttons = paymentTypeRepository.getByDealTypeAndIsOn(DealType.BUY, Boolean.TRUE).stream()
                .map(paymentType -> InlineButton.builder()
                        .text(paymentType.getName())
                        .data(paymentType.getPid().toString())
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build())
                .collect(Collectors.toList());

        buttons.add(KeyboardUtil.INLINE_BACK_BUTTON);

        ReplyKeyboard keyboard = KeyboardUtil.buildInline(buttons);
        responseSender.sendMessage(chatId, message, keyboard, "HTML");
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
            dealRepository.uppateIsPersonalAppliedByPid(currentDealPid, false);
            exchangeServiceNew.askForSum(chatId,
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
            BigDecimal rankDiscount = BigDecimalUtil.multiplyHalfUp(commission, CalculateUtil.getPercentsFactor(
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
        if (!paymentType.getDynamicOn() || paymentRequisite.size() == 1) {
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
//        BigDecimal personalBuy = USERS_PERSONAL_BUY.get(chatId);
//        if ((Objects.isNull(personalBuy) || !BigDecimal.ZERO.equals(personalBuy))
//                && BooleanUtils.isNotTrue(deal.getPersonalApplied())) {
//            personalBuy = userDiscountRepository.getPersonalBuyByChatId(chatId);
//            if (Objects.nonNull(personalBuy) && !BigDecimal.ZERO.equals(personalBuy)) {
//                if (BigDecimal.ZERO.compareTo(personalBuy) > 0) {
//                    dealAmount = dealAmount.subtract(ConverterUtil.getPercentsFactor(dealAmount).multiply(personalBuy));
//                } else {
//                    dealAmount = dealAmount.add(ConverterUtil.getPercentsFactor(dealAmount).multiply(personalBuy));
//                }
//                deal.setPersonalApplied(true);
//            }
//            if (Objects.nonNull(personalBuy)) {
//                putToUsersPersonalBuy(chatId, personalBuy);
//            } else {
//                putToUsersPersonalBuy(chatId, BigDecimal.ZERO);
//            }
//        }
//        BigDecimal bulkDiscount = BulkDiscountUtil.getPercentBySum(dealAmount);
//        if (!BigDecimal.ZERO.equals(bulkDiscount) && BooleanUtils.isNotTrue(deal.getBulkApplied())) {
//            dealAmount = dealAmount.subtract(ConverterUtil.getPercentsFactor(dealAmount).multiply(bulkDiscount));
//            deal.setBulkApplied(true);
//        }
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
                .toPlainString() + "₽</code>"
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
        dealService.updateIsCurrentByPid(false, currentDealPid);
        Deal deal = dealService.getByPid(currentDealPid);
        if (BooleanUtils.isTrue(deal.getUsedReferralDiscount())) {
            Integer referralBalance = userService.getReferralBalanceByChatId(UpdateUtil.getChatId(update));
            deal.setOriginalPrice(deal.getAmount());
            if (referralBalance <= deal.getAmount().intValue()) {
                deal.setAmount(deal.getAmount().subtract(BigDecimal.valueOf(referralBalance)));
            } else {
                deal.setAmount(BigDecimal.ZERO);
            }
            dealService.save(deal);
        }
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
        responseSender.sendMessage(UpdateUtil.getChatId(update), "Отправьте скрин перевода. ",
                KeyboardUtil.buildReply(
                        List.of(ReplyButton.builder().text(Command.RECEIPTS_CANCEL_DEAL.getText())
                                .build())));
    }

}
