package tgb.btc.rce.service.processors.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentReceipt;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.CalculatorQueryException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.PaymentReceiptRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.service.schedule.DealDeleteScheduler;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.CalculatorQuery;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.calculate.DealAmount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ExchangeService {
    private KeyboardService keyboardService;

    private MessageService messageService;

    private DealRepository dealRepository;

    private UserRepository userRepository;

    private IResponseSender responseSender;

    private UserDiscountService userDiscountService;

    private CalculateService calculateService;

    private InlineQueryService queryCalculatorService;

    private BotMessageService botMessageService;

    private PaymentTypeRepository paymentTypeRepository;

    private PaymentTypeService paymentTypeService;

    private PaymentRequisiteService paymentRequisiteService;

    private AdminService adminService;

    private PaymentReceiptRepository paymentReceiptRepository;

    private DealService dealService;

    private ICalculatorTypeService calculatorTypeService;

    @Autowired
    public void setCalculatorTypeService(ICalculatorTypeService calculatorTypeService) {
        this.calculatorTypeService = calculatorTypeService;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Autowired
    public void setPaymentReceiptRepository(PaymentReceiptRepository paymentReceiptRepository) {
        this.paymentReceiptRepository = paymentReceiptRepository;
    }

    @Autowired
    public void setPaymentTypeService(PaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }

    @Autowired
    public void setPaymentRequisiteService(PaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public void setQueryCalculatorService(InlineQueryService queryCalculatorService) {
        this.queryCalculatorService = queryCalculatorService;
    }

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserDiscountService(UserDiscountService userDiscountService) {
        this.userDiscountService = userDiscountService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }


    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    public void askForFiatCurrency(Long chatId) {
        responseSender.sendMessage(chatId, "Выберите валюту.", keyboardService.getFiatCurrencies());
    }

    public boolean saveFiatCurrency(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        FiatCurrency fiatCurrency;
        boolean isFew = FiatCurrencyUtil.isFew();
        log.debug("isFew = " + isFew);
        if (isFew) {
            if (!update.hasCallbackQuery()) {
                responseSender.sendMessage(chatId, "Выберите валюту.");
                return false;
            }
            fiatCurrency = FiatCurrency.fromCallbackQuery(update.getCallbackQuery());
        } else {
            fiatCurrency = FiatCurrencyUtil.getFirst();
            if (Objects.isNull(fiatCurrency)) log.debug("FiatCurrencyUtil.getFirst() == null");
            else log.debug("fiatCurrency = " + fiatCurrency.name());
        }
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        log.debug("currentDealPid=" + currentDealPid);
        dealRepository.updateFiatCurrencyByPid(currentDealPid, fiatCurrency);
        log.debug("сохранен фиат");
        return true;
    }

    public void askForCryptoCurrency(Long chatId) {
        DealType dealType = dealRepository.getDealTypeByPid(userRepository.getCurrentDealByChatId(chatId));
        messageService.sendMessageAndSaveMessageId(chatId, MessagePropertiesUtil.getChooseCurrency(dealType),
                keyboardService.getCurrencies(dealType));
    }

    public boolean saveCryptoCurrency(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выберите валюту.");
            return false;
        }
        CryptoCurrency currency = CryptoCurrency.valueOf(update.getCallbackQuery().getData());
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        dealRepository.updateCryptoCurrencyByPid(currentDealPid, currency);
        return true;
    }

    public boolean alreadyHasDeal(Long chatId) {
        if (dealRepository.getActiveDealsCountByUserChatId(chatId) > 0) {
            responseSender.sendMessage(chatId, "У вас уже есть активная заявка.",
                    InlineButton.buildData("Удалить", Command.DELETE_DEAL.getText()));
            return true;
        }
        return false;
    }

    public void sendTotalDealAmount(Long chatId, DealType dealType, CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency) {
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        BigDecimal dealAmount = dealRepository.getAmountByPid(currentDealPid);
        BigDecimal cryptoAmount = dealRepository.getCryptoAmountByPid(currentDealPid);
        String message;
        if (DealType.isBuy(dealType)) {
            message = "Сумма к получению: " + BigDecimalUtil.roundToPlainString(cryptoAmount, cryptoCurrency.getScale())
                    + " " + cryptoCurrency.getDisplayName() + "\n"
                    + "Сумма к оплате: " + BigDecimalUtil.roundToPlainString(dealAmount) + " "
                    + fiatCurrency.getDisplayName() + "\n";
            if (BooleanUtils.isTrue(FunctionPropertiesUtil.getSumToReceive(cryptoCurrency))) {
                message = message.concat("Сумма к зачислению: "
                        + BigDecimalUtil.roundToPlainString(calculateService.convertToFiat(
                        dealRepository.getCryptoCurrencyByPid(currentDealPid),
                        dealRepository.getFiatCurrencyByPid(currentDealPid),
                        cryptoAmount))
                        + " " + fiatCurrency.getDisplayName());
            }
        } else message = "Сумма к получению: " + BigDecimalUtil.roundToPlainString(dealAmount) + " "
                + fiatCurrency.getDisplayName() + "\n"
                + "Сумма к оплате: " + BigDecimalUtil.roundToPlainString(cryptoAmount, cryptoCurrency.getScale())
                + " " + cryptoCurrency.getShortName();
        responseSender.sendMessage(chatId, message);
    }

    public boolean calculateDealAmount(Long chatId, BigDecimal enteredAmount) {
        return calculateDealAmount(chatId, enteredAmount, null);
    }

    public boolean calculateDealAmount(Long chatId, BigDecimal enteredAmount, Boolean isEnteredInCrypto) {
        Deal deal = dealRepository.findByPid(userRepository.getCurrentDealByChatId(chatId));
        DealAmount dealAmount = calculateService.calculate(chatId, enteredAmount,
                deal.getCryptoCurrency(), deal.getFiatCurrency(),
                deal.getDealType(), isEnteredInCrypto);
        if (isLessThanMin(chatId, deal.getDealType(), deal.getCryptoCurrency(), dealAmount.getCryptoAmount())) {
            return false;
        }
        dealAmount.updateDeal(deal);
        dealRepository.save(deal);
        return true;
    }

    private boolean isLessThanMin(Long chatId, DealType dealType, CryptoCurrency cryptoCurrency, BigDecimal dealCryptoAmount) {
        BigDecimal minSum = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.MIN_SUM, dealType, cryptoCurrency);
        if (dealCryptoAmount.compareTo(minSum) < 0) {
            responseSender.sendMessage(chatId, "Минимальная сумма " + (DealType.isBuy(dealType)
                    ? "покупки"
                    : "продажи")
                    + " " + cryptoCurrency.getDisplayName()
                    + " = " + minSum.stripTrailingZeros().toPlainString() + ".");
            return true;
        }
        return false;
    }

    public void calculateForInlineQuery(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String inlineQueryId = update.getInlineQuery().getId();
        CalculatorQuery calculatorQuery;
        try {
            calculatorQuery = new CalculatorQuery(queryCalculatorService.getQueryWithPoints(update));
        } catch (CalculatorQueryException e) {
            responseSender.sendAnswerInlineQuery(inlineQueryId, "Введите сумму.", null, "Ошибка");
            return;
        }
        BigDecimal enteredAmount = calculatorQuery.getEnteredAmount();

        DealAmount dealAmount = calculateService.calculate(chatId, enteredAmount,
                calculatorQuery.getCurrency(),
                calculatorQuery.getFiatCurrency(),
                calculatorQuery.getDealType());
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        CryptoCurrency cryptoCurrency = dealRepository.getCryptoCurrencyByPid(currentDealPid);
        BigDecimal minSum = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.MIN_SUM,
                dealType, cryptoCurrency);
        if (dealAmount.getCryptoAmount().compareTo(minSum) < 0) {
            responseSender.sendAnswerInlineQuery(inlineQueryId, "Минимальная сумма " + dealType.getAccusative()
                            + " равна " + BigDecimalUtil.roundToPlainString(minSum, cryptoCurrency.getScale()), null,
                    "Ошибка");
            return;
        }

        String resultText = calculatorQuery.getDealType().getNominative() + ": "
                + BigDecimalUtil.roundToPlainString(dealAmount.getCryptoAmount(), calculatorQuery.getCurrency().getScale())
                + " ~ " + BigDecimalUtil.roundToPlainString(dealAmount.getAmount(), 0);
        responseSender.sendAnswerInlineQuery(inlineQueryId, resultText, "Нажмите сюда, чтобы отправить сумму.",
                BigDecimalUtil.toPlainString(calculatorQuery.getEnteredAmount()));
    }

    public void askForUserRequisites(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.findByPid(userRepository.getCurrentDealByChatId(chatId));
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        ReplyKeyboard keyboard;
        String message;
        if (DealType.isBuy(deal.getDealType())) {
            message = "\uD83D\uDCDDВведите " + cryptoCurrency.getDisplayName()
                    + "-адрес кошелька, куда вы хотите отправить "
                    + BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale())
                    + " " + cryptoCurrency.getShortName();
            List<InlineButton> buttons = new ArrayList<>();

            if (dealRepository.getPassedDealsCountByUserChatIdAndDealTypeAndCryptoCurrency(chatId, DealType.BUY,
                    cryptoCurrency) > 0) {
                String wallet = dealRepository.getWalletFromLastPassedByChatIdAndDealTypeAndCryptoCurrency(chatId,
                        DealType.BUY,
                        cryptoCurrency);
                message = message.concat("\n\nВы можете использовать ваш сохраненный адрес:\n" + wallet);
                buttons.add(BotInlineButton.USE_SAVED_WALLET.getButton());
            }
            buttons.add(BotInlineButton.CANCEL.getButton());
            keyboard = KeyboardUtil.buildInline(buttons);
        } else {
            message = "Введите " + deal.getPaymentType().getName() + " реквизиты, куда вы хотите получить "
                    + BigDecimalUtil.roundToPlainString(deal.getAmount(), 0) + " "
                    + deal.getFiatCurrency().getDisplayName();
            keyboard = BotKeyboard.INLINE_CANCEL.getKeyboard();
        }
        responseSender.sendMessage(chatId, message, keyboard, "HTML")
                .ifPresent(sentMessage -> userRepository.updateBufferVariable(chatId,
                        sentMessage.getMessageId().toString()));
    }

    public void askForUserPromoCode(Long chatId) {
        Deal deal = dealRepository.findByPid(userRepository.getCurrentDealByChatId(chatId));
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        BigDecimal dealAmount = deal.getAmount();
        BigDecimal promoCodeDiscount = BotVariablePropertiesUtil.getBigDecimal(
                BotVariableType.PROMO_CODE_DISCOUNT.getKey());
        BigDecimal discount = BigDecimalUtil.multiplyHalfUp(deal.getCommission(),
                calculateService.getPercentsFactor(promoCodeDiscount));
        dealRepository.updateDiscountByPid(discount, deal.getPid());
        BigDecimal sumWithDiscount = dealAmount.subtract(discount);

        String message = "<b>Покупка " + cryptoCurrency.getDisplayName() + "</b>: "
                + BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale()) + "\n\n"
                + "<b>Сумма перевода</b>: <s>" + BigDecimalUtil.roundToPlainString(dealAmount)
                + "</s> " + BigDecimalUtil.roundToPlainString(sumWithDiscount) + "\n\n"
                + "\uD83C\uDFAB У вас есть промокод: <b>" + BotVariablePropertiesUtil.getVariable(
                BotVariableType.PROMO_CODE_NAME)
                + "</b>, который даёт скидку в размере " + BigDecimalUtil.roundToPlainString(
                promoCodeDiscount) + "% от комиссии"
                + "\n\n"
                + "✅ <b>Использовать промокод</b> как скидку?";
        responseSender.sendMessage(chatId, message, keyboardService.getPromoCode(sumWithDiscount, dealAmount), "HTML");
    }

    public void processPromoCode(Update update) {
        if (!update.hasCallbackQuery()) {
            throw new BaseException("Ожидался CallbackQuery с USE_PROMO.");
        }
        boolean isUsed = isUsePromoData(update.getCallbackQuery().getData());
        dealRepository.updateIsUsedPromoByPid(isUsed,
                userRepository.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
    }

    private boolean isUsePromoData(String data) {
        return BotStringConstants.USE_PROMO.equals(data);
    }

    public boolean saveRequisites(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userRepository.getCurrentDealByChatId(UpdateUtil.getChatId(update));
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        String wallet;
        if (DealType.isBuy(dealType)) {
            if (update.hasMessage()) {
                wallet = UpdateUtil.getMessageText(update);
                try {
                    validateWallet(wallet);
                } catch (BaseException e) {
                    responseSender.sendMessage(chatId, e.getMessage());
                    return false;
                }
            } else {
                if (!update.hasCallbackQuery()
                        || !update.getCallbackQuery().getData().equals(BotStringConstants.USE_SAVED_WALLET)) return false;
                wallet = dealRepository.getWalletFromLastPassedByChatIdAndDealTypeAndCryptoCurrency(
                        chatId, dealType, dealRepository.getCryptoCurrencyByPid(currentDealPid));
            }
        } else {
            wallet = UpdateUtil.getMessageText(update);
        }
        dealRepository.updateWalletByPid(wallet, currentDealPid);
        return true;
    }


    private void validateWallet(String wallet) {
        if (wallet.length() < 33) {
            throw new BaseException("Длина кошелька не может быть меньше 33-х символов.");
        }
    }

    public void askForPaymentType(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.findByPid(userRepository.getCurrentDealByChatId(chatId));
        BigDecimal dealAmount = deal.getAmount();
        String displayCurrencyName = deal.getCryptoCurrency().getDisplayName();
        String additionalText;
        try {
            additionalText = botMessageService.findByTypeThrows(BotMessageType.ADDITIONAL_DEAL_TEXT).getText() + "\n\n";
        } catch (BaseException e) {
            additionalText = StringUtils.EMPTY;
        }
        StringBuilder messageNew = new StringBuilder();
        messageNew.append("\uD83D\uDCAC<b>Информация по заявке</b>\n" + "\uD83D\uDCAC<b>")
                .append(deal.getDealType().getNominativeFirstLetterToUpper()).append(" ")
                .append(displayCurrencyName).append("</b>: ")
                .append(BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()))
                .append("\n");
        if (DealType.isBuy(deal.getDealType())) {
            dealAmount = userDiscountService.applyDealDiscounts(chatId, dealAmount, deal.getUsedPromo(),
                    deal.getUsedReferralDiscount(), deal.getDiscount());
            messageNew.append("\uD83D\uDCB5<b>Сумма перевода</b>: ")
                    .append(BigDecimalUtil.roundToPlainString(dealAmount))
                    .append(" ").append(deal.getFiatCurrency().getDisplayName()).append("\n\n")
                    .append(additionalText).append("<b>Выберите способ оплаты:</b>");
        } else {
            messageNew.append("\n\n" + "\uD83D\uDCB5<b>Сумма перевода</b>: ")
                    .append(BigDecimalUtil.roundToPlainString(dealAmount)).append(" ")
                    .append(deal.getFiatCurrency().getDisplayName()).append("\n\n")
                    .append(additionalText).append("<b>Выберите способ получения перевода:</b>");
        }

        responseSender.sendMessage(chatId, messageNew.toString(),
                keyboardService.getPaymentTypes(deal.getDealType(), deal.getFiatCurrency()), "HTML");
    }

    public Boolean savePaymentType(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        PaymentType paymentType;
        if (!isFewPaymentTypes(chatId)) {
            try {
                paymentType = paymentTypeService.getFirstTurned(userRepository.getCurrentDealByChatId(chatId));
            } catch (BaseException e) {
                responseSender.sendMessage(chatId, e.getMessage());
                return false;
            }
        } else {
            if (!update.hasCallbackQuery()) {
                responseSender.sendMessage(chatId, "Выберите способ оплаты.");
                return false;
            }
            responseSender.deleteCallbackMessageIfExists(update);
            paymentType = paymentTypeRepository.getByPid(Long.parseLong(update.getCallbackQuery().getData()));
        }
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        if (paymentType.getMinSum().compareTo(dealRepository.getAmountByPid(currentDealPid)) > 0) {
            responseSender.sendMessage(chatId, "Минимальная сумма для " + dealType.getGenitive() + " через "
                    + paymentType.getName() + " равна " + paymentType.getMinSum().toPlainString());
            userRepository.updateStepByChatId(chatId, 2);
            calculatorTypeService.run(update);
            return false;
        }
        dealRepository.updatePaymentTypeByPid(paymentType, currentDealPid);
        return true;
    }

    public boolean isFewPaymentTypes(Long chatId) {
        return paymentTypeService.getTurnedCountByDeal(chatId) > 1;
    }

    public void buildDeal(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.findByPid(userRepository.getCurrentDealByChatId(chatId));
        CryptoCurrency currency = deal.getCryptoCurrency();
        String message;
        Long countPassed = dealRepository.getCountPassedByUserChatId(chatId);
        Rank rank = Objects.nonNull(countPassed)
                ? Rank.getByDealsNumber(countPassed.intValue())
                : Rank.FIRST;
        BigDecimal dealAmount = userDiscountService.applyRank(rank, deal);

        String promoCodeText = Boolean.TRUE.equals(deal.getUsedPromo())
                ? "\n\n<b> Использован скидочный промокод</b>: "
                        + BotVariablePropertiesUtil.getVariable(BotVariableType.PROMO_CODE_NAME) + "\n\n"
                : "\n\n";

        PaymentType paymentType = deal.getPaymentType();
        deal.setDateTime(LocalDateTime.now());
        if (DealType.isBuy(deal.getDealType())) {
            dealAmount = userDiscountService.applyDealDiscounts(chatId, dealAmount, deal.getUsedPromo(),
                    deal.getUsedReferralDiscount(), deal.getDiscount());

            String requisite;
            try {
                requisite = paymentRequisiteService.getRequisite(paymentType);
            } catch (BaseException e) {
                responseSender.sendMessage(chatId, e.getMessage());
                return;
            }

            message = "✅<b>Заявка №</b><code>" + deal.getPid() + "</code> успешно создана." + "\n\n"
                    + "<b>Получаете</b>: " + BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(),
                    currency.getScale())
                    + " " + currency.getShortName() + "\n"
                    + "<b>" + deal.getCryptoCurrency()
                    .getDisplayName() + "-адрес</b>:" + "<code>" + deal.getWallet() + "</code>" + "\n\n"
                    + "Ваш ранг: " + rank.getSmile() + ", скидка " + rank.getPercent() + "%" + "\n\n"
                    + "<b>\uD83D\uDCB5Сумма к оплате</b>: <code>" + BigDecimalUtil.roundToPlainString(dealAmount, 0)
                    + " " + deal.getFiatCurrency().getDisplayName() + "</code>" + "\n"
                    + "<b>Резквизиты для оплаты:</b>" + "\n\n"
                    + "<code>" + requisite + "</code>" + "\n\n"
                    + "<b>⏳Заявка действительна</b>: " + BotVariablePropertiesUtil.getVariable(
                    BotVariableType.DEAL_ACTIVE_TIME) + " минут" + "\n\n"
                    + "☑️После успешного перевода денег по указанным реквизитам нажмите на кнопку <b>\""
                    + Command.PAID.getText() + "\"</b> или же вы можете отменить данную заявку, нажав на кнопку <b>\""
                    + Command.CANCEL_DEAL.getText() + "\"</b>."
                    + promoCodeText;
        } else {
            message = "✅<b>Заявка №</b><code>" + deal.getPid() + "</code> успешно создана." + "\n\n"
                    + "<b>Продаете</b>: "
                    + BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(),
                    currency.getScale()) + " " + currency.getShortName() + "\n"
                    + "<b>" + paymentType.getName() + " реквизиты</b>:" + "<code>" + deal.getWallet() + "</code>" + "\n\n"
                    + "Ваш ранг: " + rank.getSmile() + ", скидка " + rank.getPercent() + "%" + "\n\n"
                    + "\uD83D\uDCB5<b>Получаете</b>: <code>" + BigDecimalUtil.roundToPlainString(dealAmount)
                    + " " + deal.getFiatCurrency().getDisplayName() + "</code>" + "\n"
                    + "<b>Реквизиты для перевода " + currency.getShortName() + ":</b>" + "\n\n"
                    + "<code>" + BotVariablePropertiesUtil.getWallet(currency) + "</code>" + "\n\n"
                    + "⏳<b>Заявка действительна</b>: " + BotVariablePropertiesUtil.getVariable(
                    BotVariableType.DEAL_ACTIVE_TIME) + " минут" + "\n\n"
                    + "☑️После успешного перевода денег по указанному кошельку нажмите на кнопку <b>\""
                    + Command.PAID.getText() + "\"</b> или же вы можете отменить данную заявку, нажав на кнопку <b>\""
                    + Command.CANCEL_DEAL.getText() + "\"</b>."
                    + promoCodeText;
        }
        deal.setAmount(dealAmount);
        dealRepository.save(deal);

        Optional<Message> optionalMessage = responseSender.sendMessage(chatId, message,
                BotKeyboard.BUILD_DEAL.getKeyboard(), "HTML");
        if (DealType.isBuy(deal.getDealType())) {
            DealDeleteScheduler.addNewCryptoDeal(deal.getPid(),
                    optionalMessage.map(
                                    Message::getMessageId)
                            .orElseThrow(
                                    () -> new BaseException(
                                            "Ошибка при получении messageId.")));
        }
    }

    public Boolean isPaid(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Для отмены сделки нажми \"Отменить сделку\".");
            return null;
        }
        Long dealPid = userRepository.getCurrentDealByChatId(chatId);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        if (Command.PAID.name().equals(update.getCallbackQuery().getData())) {
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            askForReceipts(update);
            userRepository.nextStep(chatId);
            return true;
        } else {
            cancelDeal(CallbackQueryUtil.messageId(update), chatId, dealPid);
            return false;
        }
    }

    public void cancelDeal(Integer messageId, Long chatId, Long dealPid) {
        responseSender.deleteMessage(chatId, messageId);
        dealRepository.deleteById(dealPid);
        userRepository.updateCurrentDealByChatId(null, chatId);
        responseSender.sendMessage(chatId, "Заявка отменена.");
    }

    public void confirmDeal(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        dealRepository.updateIsActiveByPid(true, currentDealPid);
        userRepository.setDefaultValues(chatId);
        responseSender.sendMessage(chatId, MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_CONFIRMED));
        adminService.notify("Поступила новая заявка на " + dealType.getGenitive() + ".",
                keyboardService.getShowDeal(currentDealPid));
    }

    public void askForReferralDiscount(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        BigDecimal dealAmount = dealRepository.getAmountByPid(currentDealPid);
        if (BooleanUtils.isTrue(dealRepository.getIsUsedPromoByPid(currentDealPid))) {
            dealAmount = dealAmount.subtract(dealRepository.getDiscountByPid(currentDealPid));
        }
        Integer referralBalance = userRepository.getReferralBalanceByChatId(chatId);

        BigDecimal sumWithDiscount;
        if (referralBalance <= dealAmount.intValue()) {
            sumWithDiscount = dealAmount.subtract(BigDecimal.valueOf(referralBalance));
        } else {
            sumWithDiscount = BigDecimal.ZERO;
        }

        String message = "\uD83E\uDD11У вас есть " + referralBalance + "₽ на реферальном балансе. Использовать их в качестве скидки?";
        responseSender.sendMessage(chatId, message,
                keyboardService.getUseReferralDiscount(sumWithDiscount, dealAmount), "HTML");
    }

    public boolean processReferralDiscount(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выбери использовать со скидкой или без, либо нажми \"Назад\" " +
                    "для возвращения в предыдущее меню.");
            return false;
        }
        Boolean isUsedReferralDiscount = update.getCallbackQuery().getData().equals(BotStringConstants.USE_REFERRAL_DISCOUNT);
        dealRepository.updateUsedReferralDiscountByPid(isUsedReferralDiscount, userRepository.getCurrentDealByChatId(chatId));
        return true;
    }

    public void askForReceipts(Update update) {
        responseSender.sendMessage(UpdateUtil.getChatId(update),
                "Отправьте скрин перевода, либо чек оплаты.", BotKeyboard.CANCEL_DEAL);
    }

    public boolean saveReceipts(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.findByPid(userRepository.getCurrentDealByChatId(chatId));
        PaymentReceipt paymentReceipt;
        if (update.getMessage().hasDocument()) {
            paymentReceipt = paymentReceiptRepository.save(PaymentReceipt.builder()
                    .receipt(update.getMessage().getDocument().getFileId())
                    .receiptFormat(ReceiptFormat.PDF)
                    .build());
        } else if (update.getMessage().hasPhoto()) {
            paymentReceipt = paymentReceiptRepository.save(PaymentReceipt.builder()
                    .receipt(update.getMessage().getPhoto().get(0).getFileId())
                    .receiptFormat(ReceiptFormat.PICTURE)
                    .build());
        } else {
            responseSender.sendMessage(chatId, "Отправьте чек.");
            return false;
        }
        List<PaymentReceipt> paymentReceipts = dealService.getPaymentReceipts(deal.getPid());
        paymentReceipts.add(paymentReceipt);
        deal.setPaymentReceipts(paymentReceipts);
        dealService.save(deal);
        return true;
    }

}
