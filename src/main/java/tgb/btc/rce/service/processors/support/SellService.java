package tgb.btc.rce.service.processors.support;

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
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.exception.NumberParseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SellService {

    private final ResponseSender responseSender;
    private final UserService userService;
    private final DealService dealService;
    private final PaymentConfigService paymentConfigService;

    private final BotMessageService botMessageService;

    private UserDiscountRepository userDiscountRepository;

    private PaymentTypeRepository paymentTypeRepository;

    private DealRepository dealRepository;

    private ExchangeServiceNew exchangeServiceNew;

    private CalculateService calculateService;

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setExchangeServiceNew(ExchangeServiceNew exchangeServiceNew) {
        this.exchangeServiceNew = exchangeServiceNew;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    private final static Map<Long, BigDecimal> USERS_PERSONAL_SELL = new HashMap<>();

    public static void putToUsersPersonalSell(Long userChatId, BigDecimal personalSell) {
        if (Objects.isNull(personalSell)) {
            throw new BaseException("Персональная скидка на продажу не может быть null.");
        }
        USERS_PERSONAL_SELL.put(userChatId, personalSell);
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
    }

    @Autowired
    public SellService(ResponseSender responseSender, UserService userService, DealService dealService,
                       PaymentConfigService paymentConfigService, BotMessageService botMessageService) {
        this.responseSender = responseSender;
        this.userService = userService;
        this.dealService = dealService;
        this.paymentConfigService = paymentConfigService;
        this.botMessageService = botMessageService;
    }

    public boolean saveSum(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.findById(userService.getCurrentDealByChatId(chatId));
        Double sum = UpdateUtil.getDoubleFromText(update);
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        Double minSum = BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM, DealType.SELL, cryptoCurrency);

        if (sum < minSum) {
            responseSender.sendMessage(chatId, "Минимальная сумма продажи " + cryptoCurrency.getDisplayName()
                    + " = " + BigDecimal.valueOf(minSum).stripTrailingZeros().toPlainString() + ".");
            return false;
        }

        deal.setCryptoAmount(BigDecimal.valueOf(sum));
        BigDecimal amount = calculateService.convert(cryptoCurrency, sum, deal.getFiatCurrency(), DealType.SELL, true);
        BigDecimal personalSell = USERS_PERSONAL_SELL.get(chatId);
        if (BooleanUtils.isNotTrue(deal.getPersonalApplied())) {
            if (Objects.isNull(personalSell)) {
                personalSell = userDiscountRepository.getPersonalSellByChatId(chatId);
                if (Objects.nonNull(personalSell) && !(BigDecimal.ZERO.compareTo(personalSell) == 0)) {
                    amount = amount.subtract(calculateService.getPercentsFactor(amount).multiply(personalSell));
                    deal.setPersonalApplied(true);
                }
                if (Objects.nonNull(personalSell)) {
                    putToUsersPersonalSell(chatId, personalSell);
                } else {
                    putToUsersPersonalSell(chatId, BigDecimal.ZERO);
                }
            } else if (!BigDecimal.ZERO.equals(personalSell)) {
                amount = amount.subtract(calculateService.getPercentsFactor(amount).multiply(personalSell));
                deal.setPersonalApplied(true);
            }
        }
        deal.setAmount(amount);
        dealService.save(deal);
        dealService.updateCommissionByPid(calculateService.getCommissionForSell(BigDecimal.valueOf(sum), cryptoCurrency,
                deal.getFiatCurrency(), deal.getDealType()), deal.getPid());
        return true;
    }

    public void convertToRub(Update update, Long currentDealPid) {
        Long chatId = UpdateUtil.getChatId(update);
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
        BigDecimal minSum = BigDecimalUtil.round(
                BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM, DealType.SELL, cryptoCurrency),
                cryptoCurrency.getScale()
        );

        if (sum.doubleValue() < minSum.doubleValue()) {
            sendInlineAnswer(update, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + minSum.stripTrailingZeros().toPlainString() + ".", false);
            return;
        }
        sum = BigDecimal.valueOf(BigDecimalUtil.round(sum, cryptoCurrency.getScale()).doubleValue());
        BigDecimal roundedConvertedSum = calculateService.convert(currency, sum.doubleValue(),
                dealRepository.getFiatCurrencyByPid(currentDealPid), DealType.SELL, true);
        BigDecimal personalSell = USERS_PERSONAL_SELL.get(chatId);
        if (Objects.isNull(personalSell) || !BigDecimal.ZERO.equals(personalSell)) {
            personalSell = userDiscountRepository.getPersonalBuyByChatId(chatId);
            if (Objects.nonNull(personalSell) && !BigDecimal.ZERO.equals(personalSell)) {
                roundedConvertedSum = roundedConvertedSum.subtract(calculateService.getPercentsFactor(roundedConvertedSum).multiply(personalSell));
            }
            if (Objects.nonNull(personalSell)) {
                putToUsersPersonalSell(chatId, personalSell);
            } else {
                putToUsersPersonalSell(chatId, BigDecimal.ZERO);
            }
        }
        sendInlineAnswer(update, sum.stripTrailingZeros().toPlainString() + " " + currency.getDisplayName() + " ~ " +
                BigDecimalUtil.round(roundedConvertedSum, 0).toPlainString(), true);
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
                ? update.getInlineQuery().getQuery().substring(update.getInlineQuery().getQuery().indexOf(" "))
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

    public void askForWallet(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.findById(userService.getCurrentDealByChatId(chatId));
        String message = "Введите " + deal.getPaymentType().getName() + " реквизиты, куда вы "
                + "хотите получить "
                + BigDecimalUtil.round(deal.getAmount(), 0).stripTrailingZeros().toPlainString() + " " + deal.getFiatCurrency().getDisplayName();

        Optional<Message> optionalMessage = responseSender.sendMessage(chatId, message,
                KeyboardUtil.buildInline(
                        List.of(KeyboardUtil.INLINE_BACK_BUTTON)));
        optionalMessage.ifPresent(
                sentMessage -> userService.updateBufferVariable(chatId, sentMessage.getMessageId().toString()));
    }

    public void saveWallet(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String wallet = UpdateUtil.getMessageText(update);
        dealService.updateWalletByPid(wallet, userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
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
        String message = "<b>Информация по заявке</b>\n"
                + "\uD83D\uDCAC<b>Продажа " + displayCurrencyName + "</b>: " + dealCryptoAmount.stripTrailingZeros()
                .toPlainString()
                + "\n\n"
                + "\uD83D\uDCB5<b>Сумма перевода</b>: " + dealAmount.stripTrailingZeros().toPlainString() + " " + deal.getFiatCurrency().getDisplayName()
                + "\n\n"
                + additionalText
                + "<b>Выберите способ получения перевода:</b>";


        List<InlineButton> buttons = paymentTypeRepository.getByDealTypeAndIsOnAndFiatCurrency(DealType.SELL, Boolean.TRUE, deal.getFiatCurrency()).stream()
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
        PaymentType paymentType = paymentTypeRepository.getByPid(Long.parseLong(update.getCallbackQuery().getData()));
        Long currentDealPid = userService.getCurrentDealByChatId(UpdateUtil.getChatId(update));
        if (paymentType.getMinSum().compareTo(dealRepository.getAmountByPid(currentDealPid)) > 0) {
            Long chatId = UpdateUtil.getChatId(update);
            responseSender.sendMessage(chatId, "Минимальная сумма для продажи через "
                    + paymentType.getName() + " равна " + paymentType.getMinSum().toPlainString());
            userService.previousStep(chatId);
            currentDealPid = userService.getCurrentDealByChatId(chatId);
            dealRepository.updateIsPersonalAppliedByPid(currentDealPid, false);
            exchangeServiceNew.askForSum(chatId,
                    dealService.getCryptoCurrencyByPid(currentDealPid), DealType.SELL);
            return null;
        }
        dealService.updatePaymentTypeByPid(paymentType,
                userService.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
        return true;
    }

    public void buildDeal(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealService.getByPid(userService.getCurrentDealByChatId(chatId));
        CryptoCurrency currency = deal.getCryptoCurrency();
        String promoCodeText = Boolean.TRUE.equals(deal.getUsedPromo())
                ?
                "\n\n<b> Использован скидочный промокод</b>: "
                        + BotVariablePropertiesUtil.getVariable(BotVariableType.PROMO_CODE_NAME) + "\n\n"
                : "\n\n";

        String walletRequisites = BotVariablePropertiesUtil.getWallet(currency);

        Rank rank = Rank.getByDealsNumber(dealService.getCountPassedByUserChatId(chatId).intValue());
        boolean isRankDiscountOn = BooleanUtils.isTrue(
                BotVariablePropertiesUtil.getBoolean(BotVariableType.DEAL_RANK_DISCOUNT_ENABLE))
                && BooleanUtils.isNotFalse(userDiscountRepository.getRankDiscountByUserChatId(chatId));
        if (!Rank.FIRST.equals(rank) && isRankDiscountOn) {
            BigDecimal commission = deal.getCommission();
            BigDecimal rankDiscount = BigDecimalUtil.multiplyHalfUp(commission, calculateService.getPercentsFactor(
                    BigDecimal.valueOf(rank.getPercent())));
            deal.setAmount(BigDecimalUtil.addHalfUp(deal.getAmount(), rankDiscount));
        }
        deal = dealService.save(deal);
        String message = "✅<b>Заявка №</b><code>" + deal.getPid() + "</code> успешно создана."
                + "\n\n"
                + "<b>Продаете</b>: "
                + BigDecimalUtil.round(deal.getCryptoAmount(), currency.getScale()).stripTrailingZeros()
                .toPlainString() + " " + currency.getShortName()
                + "\n"
                + "<b>" + deal.getPaymentType().getName() + " реквизиты</b>:" + "<code>" + deal.getWallet() + "</code>"
                + "\n\n"
                + "Ваш ранг: " + rank.getSmile() + ", скидка " + rank.getPercent() + "%" + "\n\n"
                + "\uD83D\uDCB5<b>Получаете</b>: <code>" + BigDecimalUtil.round(deal.getAmount(), 0)
                .stripTrailingZeros().toPlainString() + " " + deal.getFiatCurrency().getDisplayName() + "</code>"
                + "\n"
                + "<b>Реквизиты для перевода " + currency.getShortName() + ":</b>"
                + "\n\n"
                + "<code>" + walletRequisites + "</code>"
                + "\n\n"
                + "⏳<b>Заявка действительна</b>: " + BotVariablePropertiesUtil.getVariable(
                BotVariableType.DEAL_ACTIVE_TIME) + " минут"
                + "\n\n"
                + "☑️После успешного перевода денег по указанному кошельку нажмите на кнопку <b>\""
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
    }

    public void confirmDeal(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userService.getCurrentDealByChatId(chatId);
        dealService.updateIsActiveByPid(true, currentDealPid);
        userService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_CONFIRMED));
        userService.getAdminsChatIds().forEach(adminChatId ->
                responseSender.sendMessage(adminChatId,
                        "Поступила новая заявка на продажу.",
                        KeyboardUtil.buildInline(List.of(
                                InlineButton.builder()
                                        .text(Command.SHOW_DEAL.getText())
                                        .data(Command.SHOW_DEAL.getText() + BotStringConstants.CALLBACK_DATA_SPLITTER
                                                + currentDealPid)
                                        .build()
                        ))));
        userService.updateCurrentDealByChatId(null, chatId);
    }

}
