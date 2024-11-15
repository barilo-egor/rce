package tgb.btc.rce.service.processors.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.api.web.INotificationsAPI;
import tgb.btc.library.bean.bot.Deal;
import tgb.btc.library.bean.bot.PaymentReceipt;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.constants.enums.ReferralType;
import tgb.btc.library.constants.enums.bot.*;
import tgb.btc.library.constants.enums.properties.PropertiesPath;
import tgb.btc.library.constants.enums.properties.VariableType;
import tgb.btc.library.exception.BaseException;
import tgb.btc.library.exception.CalculatorQueryException;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.library.interfaces.enums.IDeliveryTypeService;
import tgb.btc.library.interfaces.service.bean.bot.*;
import tgb.btc.library.interfaces.service.bean.bot.deal.IModifyDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.IReadDealService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealCountService;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.library.service.process.CalculateService;
import tgb.btc.library.service.properties.ButtonsDesignPropertiesReader;
import tgb.btc.library.service.properties.VariablePropertiesReader;
import tgb.btc.library.service.schedule.DealDeleteScheduler;
import tgb.btc.library.vo.calculate.DealAmount;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.*;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.process.IUserDiscountProcessService;
import tgb.btc.rce.service.util.ICallbackQueryService;
import tgb.btc.rce.service.util.ICommandService;
import tgb.btc.rce.service.util.ICryptoCurrenciesDesignService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.CalculatorQuery;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class ExchangeService {
    private IKeyboardService keyboardService;

    private IMessageService messageService;

    private IReadDealService readDealService;

    private IDealCountService dealCountService;

    private IDealPropertyService dealPropertyService;

    private IModifyDealService modifyDealService;

    private IReadUserService readUserService;

    private IModifyUserService modifyUserService;

    private IResponseSender responseSender;

    private IUserDiscountProcessService userDiscountProcessService;

    private CalculateService calculateService;

    private IBotMessageService botMessageService;

    private IPaymentTypeService paymentTypeService;

    private IPaymentRequisiteService paymentRequisiteService;

    private INotifyService notifyService;

    private IPaymentReceiptService paymentReceiptService;

    private ICalculatorTypeService calculatorTypeService;

    private INotificationsAPI notificationsAPI;

    private IKeyboardBuildService keyboardBuildService;

    private ICallbackQueryService callbackQueryService;

    private ICryptoCurrenciesDesignService cryptoCurrenciesDesignService;

    private IFunctionsService functionsService;

    private IMessagePropertiesService messagePropertiesService;

    private IUpdateService updateService;

    private IDeliveryTypeService deliveryTypeService;

    private VariablePropertiesReader variablePropertiesReader;

    private IFiatCurrencyService fiatCurrencyService;

    private IBigDecimalService bigDecimalService;

    private ICommandService commandService;

    private IModule<DeliveryKind> deliveryKindModule;

    private IModule<ReferralType> referralModule;

    private ButtonsDesignPropertiesReader buttonsDesignPropertiesReader;

    private ISecurePaymentDetailsService securePaymentDetailsService;

    private IMessageImageResponseSender messageImageResponseSender;

    private IMessageImageService messageImageService;

    @Autowired
    public void setMessageImageService(IMessageImageService messageImageService) {
        this.messageImageService = messageImageService;
    }

    @Autowired
    public void setMessageImageResponseSender(IMessageImageResponseSender messageImageResponseSender) {
        this.messageImageResponseSender = messageImageResponseSender;
    }

    @Autowired
    public void setSecurePaymentDetailsService(ISecurePaymentDetailsService securePaymentDetailsService) {
        this.securePaymentDetailsService = securePaymentDetailsService;
    }

    @Autowired
    public void setButtonsDesignPropertiesReader(ButtonsDesignPropertiesReader buttonsDesignPropertiesReader) {
        this.buttonsDesignPropertiesReader = buttonsDesignPropertiesReader;
    }

    @Autowired
    public void setDeliveryKindModule(IModule<DeliveryKind> deliveryKindModule) {
        this.deliveryKindModule = deliveryKindModule;
    }

    @Autowired
    public void setReferralModule(IModule<ReferralType> referralModule) {
        this.referralModule = referralModule;
    }

    @Autowired
    public void setCommandService(ICommandService commandService) {
        this.commandService = commandService;
    }

    @Autowired
    public void setBigDecimalService(IBigDecimalService bigDecimalService) {
        this.bigDecimalService = bigDecimalService;
    }

    @Autowired
    public void setFiatCurrencyService(IFiatCurrencyService fiatCurrencyService) {
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Autowired
    public void setVariablePropertiesReader(VariablePropertiesReader variablePropertiesReader) {
        this.variablePropertiesReader = variablePropertiesReader;
    }

    @Autowired
    public void setDeliveryTypeService(IDeliveryTypeService deliveryTypeService) {
        this.deliveryTypeService = deliveryTypeService;
    }

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setFunctionsService(IFunctionsService functionsService) {
        this.functionsService = functionsService;
    }

    @Autowired
    public void setCryptoCurrenciesDesignService(ICryptoCurrenciesDesignService cryptoCurrenciesDesignService) {
        this.cryptoCurrenciesDesignService = cryptoCurrenciesDesignService;
    }

    @Autowired
    public void setNotifyService(INotifyService notifyService) {
        this.notifyService = notifyService;
    }

    @Autowired
    public void setCallbackQueryService(ICallbackQueryService callbackQueryService) {
        this.callbackQueryService = callbackQueryService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setBotMessageService(IBotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Autowired
    public void setPaymentRequisiteService(IPaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Autowired
    public void setPaymentReceiptService(IPaymentReceiptService paymentReceiptService) {
        this.paymentReceiptService = paymentReceiptService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    @Autowired
    public void setDealCountService(IDealCountService dealCountService) {
        this.dealCountService = dealCountService;
    }

    @Autowired
    public void setDealPropertyService(IDealPropertyService dealPropertyService) {
        this.dealPropertyService = dealPropertyService;
    }

    @Autowired
    public void setModifyDealService(IModifyDealService modifyDealService) {
        this.modifyDealService = modifyDealService;
    }

    @Autowired
    public void setReadDealService(IReadDealService readDealService) {
        this.readDealService = readDealService;
    }

    @Autowired
    public void setNotificationsAPI(INotificationsAPI notificationsAPI) {
        this.notificationsAPI = notificationsAPI;
    }

    @Autowired
    public void setUserDiscountProcessService(IUserDiscountProcessService userDiscountProcessService) {
        this.userDiscountProcessService = userDiscountProcessService;
    }

    @Autowired
    public void setCalculatorTypeService(ICalculatorTypeService calculatorTypeService) {
        this.calculatorTypeService = calculatorTypeService;
    }

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Autowired
    public void setCalculateService(CalculateService calculateService) {
        this.calculateService = calculateService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setMessageService(IMessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setKeyboardService(IKeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    public void askForFiatCurrency(Long chatId) {
        messageImageResponseSender.sendMessage(MessageImage.CHOOSE_FIAT, chatId, keyboardService.getFiatCurrencies());
    }

    public boolean saveFiatCurrency(Update update) {
        Long chatId = updateService.getChatId(update);
        FiatCurrency fiatCurrency;
        boolean isFew = fiatCurrencyService.isFew();
        log.debug("isFew = " + isFew);
        if (isFew) {
            if (!update.hasCallbackQuery()) {
                responseSender.sendMessage(chatId, "Выберите валюту.");
                return false;
            }
            fiatCurrency = FiatCurrency.valueOf(callbackQueryService.getSplitData(update.getCallbackQuery(), 1));
        } else {
            fiatCurrency = fiatCurrencyService.getFirst();
        }
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        modifyDealService.updateFiatCurrencyByPid(currentDealPid, fiatCurrency);
        return true;
    }

    public void askForCryptoCurrency(Long chatId) {
        DealType dealType = dealPropertyService.getDealTypeByPid(readUserService.getCurrentDealByChatId(chatId));
        ReplyKeyboard replyKeyboard = keyboardService.getCurrencies(dealType);
        MessageImage messageImage;
        if (DealType.isBuy(dealType)) {
            messageImage = MessageImage.CHOOSE_CRYPTO_CURRENCY_BUY;
        } else {
            messageImage = MessageImage.CHOOSE_CRYPTO_CURRENCY_SELL;
        }
        messageImageResponseSender.sendMessageAndSaveMessageId(messageImage, chatId, replyKeyboard);
    }

    public boolean saveCryptoCurrency(Update update) {
        Long chatId = updateService.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выберите валюту.");
            return false;
        }
        CryptoCurrency currency = CryptoCurrency.valueOf(update.getCallbackQuery().getData());
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        modifyDealService.updateCryptoCurrencyByPid(currentDealPid, currency);
        return true;
    }

    public Integer getCountNotFinishedDeals(Long chatId) {
        return dealCountService.getCountDealByChatIdAndNotInDealStatus(chatId, Arrays.asList(DealStatus.NEW, DealStatus.CONFIRMED));
    }

    public List<Long> getListNewDeal(Long chatId) {
        return readDealService.getPidsByChatIdAndStatus(chatId, DealStatus.NEW);
    }

    public void deleteByPidIn(List<Long> pidList) {
        modifyDealService.deleteByPidIn(pidList);
    }

    public void sendTotalDealAmount(Long chatId, DealType dealType, CryptoCurrency cryptoCurrency, FiatCurrency fiatCurrency) {
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        BigDecimal dealAmount = dealPropertyService.getAmountByPid(currentDealPid);
        BigDecimal cryptoAmount = dealPropertyService.getCryptoAmountByPid(currentDealPid);
        String message = messagePropertiesService.getMessage("sum.info");
        if (Objects.isNull(message)) {
            if (DealType.isBuy(dealType)) {
                message = "Сумма к получению: " + bigDecimalService.roundToPlainString(cryptoAmount, cryptoCurrency.getScale())
                        + " " + cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency) + "\n"
                        + "Сумма к оплате: " + bigDecimalService.roundToPlainString(dealAmount) + " "
                        + fiatCurrency.getGenitive() + "\n";
                if (BooleanUtils.isTrue(functionsService.getSumToReceive(cryptoCurrency))) {
                    message = message.concat("Сумма к зачислению: "
                            + bigDecimalService.roundToPlainString(dealPropertyService.getCreditedAmountByPid(currentDealPid))
                            + " " + fiatCurrency.getGenitive());
                }
            } else message = "Сумма к получению: " + bigDecimalService.roundToPlainString(dealAmount) + " "
                    + fiatCurrency.getGenitive() + "\n"
                    + "Сумма к оплате: " + bigDecimalService.roundToPlainString(cryptoAmount, cryptoCurrency.getScale())
                    + " " + cryptoCurrency.getShortName();
        } else {
            if (DealType.isBuy(dealType)) {
                message = String.format(message, bigDecimalService.roundToPlainString(cryptoAmount, cryptoCurrency.getScale()),
                        cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency), bigDecimalService.roundToPlainString(dealAmount), fiatCurrency.getGenitive());
            } else {
                message = String.format(message, bigDecimalService.roundToPlainString(dealAmount), fiatCurrency.getGenitive(),
                        bigDecimalService.roundToPlainString(cryptoAmount, cryptoCurrency.getScale()), cryptoCurrency.getShortName());
            }
        }
        responseSender.sendMessage(chatId, message);
    }

    public boolean calculateDealAmount(Long chatId, BigDecimal enteredAmount) {
        return calculateDealAmount(chatId, enteredAmount, null);
    }

    public boolean calculateDealAmount(Long chatId, BigDecimal enteredAmount, Boolean isEnteredInCrypto) {
        Deal deal = readDealService.findByPid(readUserService.getCurrentDealByChatId(chatId));
        DealAmount dealAmount = calculateService.calculate(chatId, enteredAmount,
                deal.getCryptoCurrency(), deal.getFiatCurrency(),
                deal.getDealType(), isEnteredInCrypto, BooleanUtils.isTrue(functionsService.getSumToReceive(deal.getCryptoCurrency())));
        if (isLessThanMin(chatId, deal.getDealType(), deal.getCryptoCurrency(), dealAmount.getCryptoAmount())) {
            return false;
        }
        if (DealType.isBuy(deal.getDealType()) && isMoreThanMax(chatId, deal.getFiatCurrency(), dealAmount.getAmount())) {
            return false;
        }
        dealAmount.updateDeal(deal);
        modifyDealService.save(deal);
        return true;
    }

    private boolean isMoreThanMax(Long chatId, FiatCurrency fiatCurrency, BigDecimal amount) {
        Integer maxSum = variablePropertiesReader.getInt(VariableType.MAX_SUM, fiatCurrency, fiatCurrency.getDefaultMaxSum());
        if (amount.compareTo(new BigDecimal(maxSum)) > 0) {
            messageImageResponseSender.sendMessage(MessageImage.MAX_AMOUNT, chatId);
            return true;
        }
        return false;
    }

    private boolean isLessThanMin(Long chatId, DealType dealType, CryptoCurrency cryptoCurrency, BigDecimal dealCryptoAmount) {
        BigDecimal minSum = variablePropertiesReader.getBigDecimal(VariableType.MIN_SUM, dealType, cryptoCurrency);
        if (dealCryptoAmount.compareTo(minSum) < 0) {
            responseSender.sendMessage(chatId, "Минимальная сумма " + (DealType.isBuy(dealType)
                    ? "покупки"
                    : "продажи")
                    + " " + cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency)
                    + " = " + minSum.stripTrailingZeros().toPlainString() + ".");
            return true;
        }
        return false;
    }

    public void calculateForInlineQuery(Update update) {
        Long chatId = updateService.getChatId(update);
        String inlineQueryId = update.getInlineQuery().getId();
        CalculatorQuery calculatorQuery;
        try {
            calculatorQuery = new CalculatorQuery(update.getInlineQuery().getQuery().replaceAll(",", "."));
        } catch (CalculatorQueryException e) {
            responseSender.sendAnswerInlineQuery(inlineQueryId, "Введите сумму.", null, "Ошибка");
            return;
        }
        BigDecimal enteredAmount = calculatorQuery.getEnteredAmount();

        DealAmount dealAmount = calculateService.calculate(chatId, enteredAmount,
                calculatorQuery.getCurrency(),
                calculatorQuery.getFiatCurrency(),
                calculatorQuery.getDealType(),
                BooleanUtils.isTrue(functionsService.getSumToReceive(calculatorQuery.getCurrency())));
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        DealType dealType = dealPropertyService.getDealTypeByPid(currentDealPid);
        CryptoCurrency cryptoCurrency = dealPropertyService.getCryptoCurrencyByPid(currentDealPid);
        BigDecimal minSum = variablePropertiesReader.getBigDecimal(VariableType.MIN_SUM,
                dealType, cryptoCurrency);
        if (dealAmount.getCryptoAmount().compareTo(minSum) < 0) {
            responseSender.sendAnswerInlineQuery(inlineQueryId, "Минимальная сумма " + dealType.getAccusative()
                            + " равна " + bigDecimalService.roundToPlainString(minSum, cryptoCurrency.getScale()), null,
                    "Ошибка");
            return;
        }

        String resultText = calculatorQuery.getDealType().getNominative() + ": "
                + bigDecimalService.roundToPlainString(dealAmount.getCryptoAmount(), calculatorQuery.getCurrency().getScale())
                + " ~ " + bigDecimalService.roundToPlainString(dealAmount.getAmount(), 0);
        responseSender.sendAnswerInlineQuery(inlineQueryId, resultText, "Нажмите сюда, чтобы отправить сумму.",
                bigDecimalService.toPlainString(calculatorQuery.getEnteredAmount()));
    }

    public void askForUserRequisites(Update update) {
        Long chatId = updateService.getChatId(update);
        Deal deal = readDealService.findByPid(readUserService.getCurrentDealByChatId(chatId));
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        ReplyKeyboard keyboard;
        MessageImage messageImage;
        Integer subType;
        String lastWalletMessage = "";
        String message = null;
        if (DealType.isBuy(deal.getDealType())) {
            messageImage = MessageImage.getInputWallet(cryptoCurrency);
            subType = messageImageService.getSubType(messageImage);
            List<InlineButton> buttons = new ArrayList<>();
            if (dealCountService.getConfirmedDealsCountByUserChatIdAndDealTypeAndCryptoCurrency(chatId, DealType.BUY, cryptoCurrency) > 0) {
                lastWalletMessage = String.format(
                        messageImageService.getMessage(MessageImage.SAVED_WALLET),
                        readDealService.getWalletFromLastPassedByChatIdAndDealTypeAndCryptoCurrency(chatId, DealType.BUY, cryptoCurrency));
                buttons.add(InlineButton.builder()
                        .text(buttonsDesignPropertiesReader.getString("USE_SAVED_WALLET"))
                        .data(Command.USE_SAVED_WALLET.name())
                        .build());
            }
            buttons.add(BotInlineButton.CANCEL.getButton());
            keyboard = keyboardBuildService.buildInline(buttons);
            switch (subType) {
                case 1:
                    message = String.format(messageImageService.getMessage(messageImage),
                            cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency),
                            bigDecimalService.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale()),
                            cryptoCurrency.getShortName(),
                            lastWalletMessage);
                    break;
                case 2:
                    message = String.format(messageImageService.getMessage(messageImage),
                            bigDecimalService.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale()),
                            cryptoCurrency.getShortName(),
                            lastWalletMessage);
                    break;
            }
        } else {
            keyboard = keyboardService.getInlineCancel();
            messageImage = MessageImage.FIAT_INPUT_DETAILS;
            subType = messageImageService.getSubType(messageImage);
            switch (subType) {
                case 1:
                    message = String.format(messageImageService.getMessage(messageImage),
                            deal.getPaymentType().getName(),
                            bigDecimalService.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale()),
                            cryptoCurrency.getShortName());
                    break;
                case 2:
                    message = String.format(messageImageService.getMessage(messageImage),
                            bigDecimalService.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale()),
                            cryptoCurrency.getShortName());
                    break;
            }
        }
        messageImageResponseSender.sendMessage(messageImage, chatId, message, keyboard);
    }

    public void askForUserPromoCode(Long chatId) {
        Deal deal = readDealService.findByPid(readUserService.getCurrentDealByChatId(chatId));
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        BigDecimal dealAmount = deal.getAmount();
        BigDecimal promoCodeDiscount = variablePropertiesReader.getBigDecimal(
                VariableType.PROMO_CODE_DISCOUNT.getKey());
        BigDecimal discount = bigDecimalService.multiplyHalfUp(deal.getCommission(),
                calculateService.getPercentsFactor(promoCodeDiscount));
        modifyDealService.updateDiscountByPid(discount, deal.getPid());
        BigDecimal sumWithDiscount = dealAmount.subtract(discount);

        String message = "<b>Покупка " + cryptoCurrenciesDesignService.getDisplayName(cryptoCurrency) + "</b>: "
                + bigDecimalService.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale()) + "\n\n"
                + "<b>Сумма перевода</b>: <s>" + bigDecimalService.roundToPlainString(dealAmount)
                + "</s> " + bigDecimalService.roundToPlainString(sumWithDiscount) + "\n\n"
                + "\uD83C\uDFAB У вас есть промокод: <b>" + variablePropertiesReader.getVariable(
                VariableType.PROMO_CODE_NAME)
                + "</b>, который даёт скидку в размере " + bigDecimalService.roundToPlainString(
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
        modifyDealService.updateIsUsedPromoByPid(isUsed,
                readUserService.getCurrentDealByChatId(updateService.getChatId(update)));
    }

    private boolean isUsePromoData(String data) {
        return BotStringConstants.USE_PROMO.equals(data);
    }

    public boolean saveRequisites(Update update) {
        Long chatId = updateService.getChatId(update);
        Long currentDealPid = readUserService.getCurrentDealByChatId(updateService.getChatId(update));
        DealType dealType = dealPropertyService.getDealTypeByPid(currentDealPid);
        String wallet;
        if (DealType.isBuy(dealType)) {
            if (update.hasMessage()) {
                wallet = updateService.getMessageText(update);
                try {
                    validateWallet(wallet);
                    responseSender.deleteMessage(chatId, update.getMessage().getMessageId());
                } catch (BaseException e) {
                    responseSender.sendMessage(chatId, e.getMessage());
                    return false;
                }
            } else {
                if (!update.hasCallbackQuery()
                        || !update.getCallbackQuery().getData().equals(Command.USE_SAVED_WALLET.name()))
                    return false;
                wallet = readDealService.getWalletFromLastPassedByChatIdAndDealTypeAndCryptoCurrency(
                        chatId, dealType, dealPropertyService.getCryptoCurrencyByPid(currentDealPid));
                responseSender.deleteCallbackMessageIfExists(update);
            }
        } else {
            wallet = updateService.getMessageText(update);
        }
        modifyDealService.updateWalletByPid(wallet, currentDealPid);
        return true;
    }


    private void validateWallet(String wallet) {
        if (wallet.length() < 33) {
            throw new BaseException("Длина кошелька не может быть меньше 33-х символов.");
        }
    }

    public void askForPaymentType(Update update) {
        Long chatId = updateService.getChatId(update);
        Deal deal = readDealService.findByPid(readUserService.getCurrentDealByChatId(chatId));
        BigDecimal dealAmount = deal.getAmount();
        String displayCurrencyName = cryptoCurrenciesDesignService.getDisplayName(deal.getCryptoCurrency());
        MessageImage messageImage;
        Integer subType;
        if (DealType.isBuy(deal.getDealType())) {
            messageImage = MessageImage.PAYMENT_TYPES_BUY;
            subType = messageImageService.getSubType(messageImage);
        } else {
            messageImage = MessageImage.PAYMENT_TYPES_SELL;
            subType = messageImageService.getSubType(messageImage);
        }
        switch (subType) {
            case 1:
                messageImageResponseSender.sendMessage(messageImage, chatId,
                        keyboardService.getPaymentTypes(deal.getDealType(), deal.getFiatCurrency()));
                break;
            case 2:
                messageImageResponseSender.sendMessage(messageImage, chatId,
                        String.format(messageImageService.getMessage(messageImage),
                                deal.getDealType().getNominativeFirstLetterToUpper(),
                                displayCurrencyName,
                                bigDecimalService.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()),
                                bigDecimalService.roundToPlainString(dealAmount),
                                deal.getFiatCurrency().getGenitive()),
                        keyboardService.getPaymentTypes(deal.getDealType(), deal.getFiatCurrency()));
                break;
        }
    }

    public Boolean savePaymentType(Update update) {
        Long chatId = updateService.getChatId(update);
        PaymentType paymentType;
        if (!isFewPaymentTypes(chatId)) {
            try {
                paymentType = paymentTypeService.getFirstTurned(readUserService.getCurrentDealByChatId(chatId));
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
            paymentType = paymentTypeService.findById(Long.parseLong(update.getCallbackQuery().getData()));
        }
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        DealType dealType = dealPropertyService.getDealTypeByPid(currentDealPid);
        if (paymentType.getMinSum().compareTo(dealPropertyService.getAmountByPid(currentDealPid)) > 0) {
            responseSender.sendMessage(chatId, "Минимальная сумма для " + dealType.getGenitive() + " через "
                    + paymentType.getName() + " равна " + paymentType.getMinSum().toPlainString());
            modifyUserService.updateStepByChatId(chatId, 2);
            calculatorTypeService.run(update);
            return false;
        }
        modifyDealService.updatePaymentTypeByPid(paymentType, currentDealPid);
        return true;
    }

    public boolean isFewPaymentTypes(Long chatId) {
        return paymentTypeService.getTurnedCountByDeal(chatId) > 1;
    }

    public void buildDeal(Update update) {
        Long chatId = updateService.getChatId(update);
        Deal deal = readDealService.findByPid(readUserService.getCurrentDealByChatId(chatId));
        CryptoCurrency currency = deal.getCryptoCurrency();
        DealType dealType = deal.getDealType();
        Long countPassed = dealCountService.getCountConfirmedByUserChatId(chatId);
        Rank rank = Objects.nonNull(countPassed)
                ? Rank.getByDealsNumber(countPassed.intValue())
                : Rank.FIRST;
        BigDecimal dealAmount = userDiscountProcessService.applyRank(rank, deal);

        String promoCodeText = Boolean.TRUE.equals(deal.getUsedPromo())
                ? "\n\n<b> Использован скидочный промокод</b>: "
                + variablePropertiesReader.getVariable(VariableType.PROMO_CODE_NAME) + "\n\n"
                : "\n\n";

        PaymentType paymentType = deal.getPaymentType();
        deal.setDateTime(LocalDateTime.now());
        String message;
        String deliveryTypeText;
        if (deliveryKindModule.isCurrent(DeliveryKind.STANDARD) && DealType.isBuy(dealType) && CryptoCurrency.BITCOIN.equals(currency)) {
            deliveryTypeText = "<b>Способ доставки</b>: " + deliveryTypeService.getDisplayName(deal.getDeliveryType()) + "\n\n";
        } else {
            deliveryTypeText = "";
        }
        if (DealType.isBuy(dealType)) {
            dealAmount = userDiscountProcessService.applyDealDiscounts(chatId, dealAmount, deal.getUsedPromo(),
                    deal.getUsedReferralDiscount(), deal.getDiscount(), deal.getFiatCurrency());
            deal.setAmount(dealAmount);
            String requisite;
            if (securePaymentDetailsService.hasAccessToPaymentTypes(chatId, deal.getFiatCurrency())) {
                try {
                    requisite = paymentRequisiteService.getRequisite(paymentType);
                } catch (BaseException e) {
                    responseSender.sendMessage(chatId, e.getMessage());
                    return;
                }
            } else {
                requisite = securePaymentDetailsService.getByChatIdAndFiatCurrency(chatId, deal.getFiatCurrency()).getDetails();
            }
            deal.setDetails(requisite);

            message = getBuyMessage(deal, rank, requisite);
            if (Objects.isNull(message)) {
                message = "✅<b>Заявка №</b><code>" + deal.getPid() + "</code> успешно создана." + "\n\n"
                        + "<b>Получаете</b>: " + bigDecimalService.roundToPlainString(deal.getCryptoAmount(),
                        currency.getScale())
                        + " " + currency.getShortName() + "\n"
                        + "<b>" + cryptoCurrenciesDesignService.getDisplayName(deal.getCryptoCurrency())
                        + "-адрес</b>:" + "<code>" + deal.getWallet() + "</code>" + "\n\n"
                        + "Ваш ранг: " + rank.getSmile() + ", скидка " + rank.getPercent() + "%" + "\n\n"
                        + "<b>\uD83D\uDCB5Сумма к оплате</b>: <code>" + bigDecimalService.roundToPlainString(dealAmount, 0)
                        + " " + deal.getFiatCurrency().getGenitive() + "</code>" + "\n"
                        + "<b>Резквизиты для оплаты:</b>" + "\n\n"
                        + "<code>" + requisite + "</code>" + "\n\n"
                        + "<b>⏳Заявка действительна</b>: " + variablePropertiesReader.getVariable(
                        VariableType.DEAL_ACTIVE_TIME) + " минут" + "\n\n"
                        + deliveryTypeText
                        + "☑️После успешного перевода денег по указанным реквизитам нажмите на кнопку <b>\""
                        + commandService.getText(Command.PAID) + "\"</b> или же вы можете отменить данную заявку, нажав на кнопку <b>\""
                        + commandService.getText(Command.CANCEL_DEAL) + "\"</b>."
                        + promoCodeText;
            }
        } else {
            deal.setAmount(dealAmount);
            message = getSellMessage(deal, rank);
            if (Objects.isNull(message)) {
                message = "✅<b>Заявка №</b><code>" + deal.getPid() + "</code> успешно создана." + "\n\n"
                        + "<b>Продаете</b>: "
                        + bigDecimalService.roundToPlainString(deal.getCryptoAmount(),
                        currency.getScale()) + " " + currency.getShortName() + "\n"
                        + "<b>" + paymentType.getName() + " реквизиты</b>:" + "<code>" + deal.getWallet() + "</code>" + "\n\n"
                        + "Ваш ранг: " + rank.getSmile() + ", скидка " + rank.getPercent() + "%" + "\n\n"
                        + "\uD83D\uDCB5<b>Получаете</b>: <code>" + bigDecimalService.roundToPlainString(dealAmount)
                        + " " + deal.getFiatCurrency().getGenitive() + "</code>" + "\n"
                        + "<b>Реквизиты для перевода " + currency.getShortName() + ":</b>" + "\n\n"
                        + "<code>" + variablePropertiesReader.getWallet(currency) + "</code>" + "\n\n"
                        + "⏳<b>Заявка действительна</b>: " + variablePropertiesReader.getVariable(
                        VariableType.DEAL_ACTIVE_TIME) + " минут" + "\n\n"
                        + deliveryTypeText
                        + "☑️После успешного перевода денег по указанному кошельку нажмите на кнопку <b>\""
                        + commandService.getText(Command.PAID) + "\"</b> или же вы можете отменить данную заявку, нажав на кнопку <b>\""
                        + commandService.getText(Command.CANCEL_DEAL) + "\"</b>."
                        + promoCodeText;
            }
        }
        modifyDealService.save(deal);

        Optional<Message> optionalMessage = responseSender.sendMessage(chatId, message,
                keyboardService.getBuildDeal(), "HTML");
        if (DealType.isBuy(dealType)) {
            DealDeleteScheduler.addNewCryptoDeal(deal.getPid(),
                    optionalMessage.map(
                                    Message::getMessageId)
                            .orElseThrow(
                                    () -> new BaseException(
                                            "Ошибка при получении messageId.")));
        }
    }

    public String getSellMessage(Deal deal, Rank rank) {
        String message = messagePropertiesService.getMessage("deal.build.sell");
        if (Objects.isNull(message)) return null;
        CryptoCurrency currency = deal.getCryptoCurrency();
        return messagePropertiesService.getMessage("deal.build.sell", deal.getPid(),
                bigDecimalService.roundToPlainString(deal.getAmount(), deal.getCryptoCurrency().getScale()),
                deal.getFiatCurrency().getCode(), deal.getPaymentType().getName(),
                deal.getWallet(), rank.getSmile(), rank.getPercent(),
                bigDecimalService.roundToPlainString(deal.getCryptoAmount(), currency.getScale()), currency.getShortName(), currency.getShortName(),
                variablePropertiesReader.getWallet(currency),
                variablePropertiesReader.getVariable(VariableType.DEAL_ACTIVE_TIME));
    }

    public String getBuyMessage(Deal deal, Rank rank, String requisite) {
        String message = messagePropertiesService.getMessage("deal.build.buy");
        if (Objects.isNull(message)) return null;
        CryptoCurrency currency = deal.getCryptoCurrency();
        return messagePropertiesService.getMessage("deal.build.buy", deal.getPid(),
                bigDecimalService.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()), currency.getShortName(),
                cryptoCurrenciesDesignService.getDisplayName(currency),
                deal.getWallet(), rank.getSmile(), rank.getPercent() + "%",
                bigDecimalService.roundToPlainString(deal.getAmount()), deal.getFiatCurrency().getGenitive(), requisite,
                variablePropertiesReader.getVariable(VariableType.DEAL_ACTIVE_TIME));
    }

    public Boolean isPaid(Update update) {
        Long chatId = updateService.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Для отмены сделки нажми \"Отменить сделку\".");
            return null;
        }
        Long dealPid = readUserService.getCurrentDealByChatId(chatId);
        DealDeleteScheduler.deleteCryptoDeal(dealPid);
        if (!readDealService.existsById(dealPid)) {
            Integer dealActiveTime = variablePropertiesReader.getInt(VariableType.DEAL_ACTIVE_TIME);
            responseSender.sendMessage(chatId, String.format(messagePropertiesService.getMessage("deal.deleted.auto"), dealActiveTime));
            return false;
        }
        if (Command.PAID.name().equals(update.getCallbackQuery().getData())) {
            responseSender.sendEditedMessageText(chatId, update.getCallbackQuery().getMessage().getMessageId(),
                    update.getCallbackQuery().getMessage().getText(), null);
//            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            askForReceipts(update);
            modifyUserService.nextStep(chatId);
            return true;
        } else {
            cancelDeal(callbackQueryService.messageId(update), chatId, dealPid);
            return false;
        }
    }

    public void cancelDeal(Integer messageId, Long chatId, Long dealPid) {
        responseSender.deleteMessage(chatId, messageId);
        modifyDealService.deleteById(dealPid);
        modifyUserService.updateCurrentDealByChatId(null, chatId);
        responseSender.sendMessage(chatId, "Заявка отменена.");
    }

    public void confirmDeal(Update update) {
        Long chatId = updateService.getChatId(update);
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        DealDeleteScheduler.deleteCryptoDeal(currentDealPid);
        DealType dealType = dealPropertyService.getDealTypeByPid(currentDealPid);
        modifyDealService.updateDealStatusByPid(DealStatus.PAID, currentDealPid);
        modifyUserService.updateCurrentDealByChatId(null, chatId);
        modifyUserService.setDefaultValues(chatId);
        responseSender.sendMessage(chatId, messagePropertiesService.getMessage(PropertiesMessage.DEAL_CONFIRMED));
        log.info("Сделка " + currentDealPid + " пользователя " + chatId + " переведена в статус PAID");
        notifyService.notifyMessage("Поступила новая заявка на " + dealType.getGenitive() + ".",
                keyboardService.getShowDeal(currentDealPid), UserRole.OBSERVER_ACCESS);
        notificationsAPI.newBotDeal(currentDealPid);
    }

    public void askForReferralDiscount(Update update) {
        Long chatId = updateService.getChatId(update);
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        BigDecimal dealAmount = dealPropertyService.getAmountByPid(currentDealPid);
        if (BooleanUtils.isTrue(dealPropertyService.getIsUsedPromoByPid(currentDealPid))) {
            dealAmount = dealAmount.subtract(dealPropertyService.getDiscountByPid(currentDealPid));
        }
        BigDecimal sumWithDiscount;
        String message;
        Integer referralBalance = readUserService.getReferralBalanceByChatId(chatId);
        if (referralModule.isCurrent(ReferralType.STANDARD) && !FiatCurrency.BYN.equals(readDealService.findByPid(currentDealPid).getFiatCurrency())) {
            if (referralBalance <= dealAmount.intValue()) {
                sumWithDiscount = dealAmount.subtract(BigDecimal.valueOf(referralBalance));
            } else {
                sumWithDiscount = BigDecimal.ZERO;
            }
            message = "\uD83E\uDD11У вас есть " + referralBalance + "₽ на реферальном балансе. Использовать их в качестве скидки?";
        } else {
            BigDecimal refBalance = BigDecimal.valueOf(referralBalance);
            if (PropertiesPath.VARIABLE_PROPERTIES.isNotBlank("course.rub.byn")) {
                refBalance = BigDecimal.valueOf(referralBalance).multiply(PropertiesPath.VARIABLE_PROPERTIES.getBigDecimal("course.rub.byn"));
            }
            if (refBalance.compareTo(dealAmount) < 1) {
                sumWithDiscount = dealAmount.subtract(refBalance);
            } else {
                sumWithDiscount = BigDecimal.ZERO;
            }
            message = "\uD83E\uDD11У вас есть " + referralBalance + "₽(" + bigDecimalService.roundToPlainString(refBalance, 2)
                    + " бел.рублей) на реферальном балансе. Использовать их в качестве скидки?";
        }
        responseSender.sendMessage(chatId, message,
                keyboardService.getUseReferralDiscount(sumWithDiscount, dealAmount), "HTML");
    }

    public boolean processReferralDiscount(Update update) {
        Long chatId = updateService.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выбери использовать со скидкой или без, либо нажми \"Назад\" " +
                    "для возвращения в предыдущее меню.");
            return false;
        }
        Boolean isUsedReferralDiscount = update.getCallbackQuery().getData().equals(BotStringConstants.USE_REFERRAL_DISCOUNT);
        modifyDealService.updateUsedReferralDiscountByPid(isUsedReferralDiscount, readUserService.getCurrentDealByChatId(chatId));
        return true;
    }

    public void askForReceipts(Update update) {
        responseSender.sendMessage(updateService.getChatId(update),
                "Отправьте скрин перевода, либо чек оплаты.", keyboardService.getCancelDeal());
    }

    public boolean saveReceipts(Update update) {
        Long chatId = updateService.getChatId(update);
        Deal deal = readDealService.findByPid(readUserService.getCurrentDealByChatId(chatId));
        PaymentReceipt paymentReceipt;
        if (update.getMessage().hasDocument()) {
            paymentReceipt = paymentReceiptService.save(PaymentReceipt.builder()
                    .receipt(update.getMessage().getDocument().getFileId())
                    .receiptFormat(ReceiptFormat.PDF)
                    .build());
        } else if (update.getMessage().hasPhoto()) {
            List<PhotoSize> photoSizes = update.getMessage().getPhoto();
            photoSizes.sort((p1, p2) -> p2.getHeight().compareTo(p1.getHeight()));
            paymentReceipt = paymentReceiptService.save(PaymentReceipt.builder()
                    .receipt(photoSizes.get(0).getFileId())
                    .receiptFormat(ReceiptFormat.PICTURE)
                    .build());
        } else {
            responseSender.sendMessage(chatId, "Отправьте чек.");
            return false;
        }
        List<PaymentReceipt> paymentReceipts = readDealService.getPaymentReceipts(deal.getPid());
        paymentReceipts.add(paymentReceipt);
        deal.setPaymentReceipts(paymentReceipts);
        modifyDealService.save(deal);
        return true;
    }

    public void askForDeliveryType(Long chatId, FiatCurrency fiatCurrency, DealType dealType, CryptoCurrency cryptoCurrency) {
        responseSender.sendMessage(chatId, messagePropertiesService.getMessage(PropertiesMessage.DELIVERY_TYPE_ASK),
                keyboardService.getDeliveryTypes(fiatCurrency, dealType, cryptoCurrency));
    }

    public void saveDeliveryTypeAndUpdateAmount(Update update) {
        Long chatId = updateService.getChatId(update);
        DeliveryType deliveryType;
        Long dealPid = readUserService.getCurrentDealByChatId(chatId);
        DealType dealType = dealPropertyService.getDealTypeByPid(dealPid);
        CryptoCurrency cryptoCurrency = dealPropertyService.getCryptoCurrencyByPid(dealPid);
        if (deliveryKindModule.isCurrent(DeliveryKind.STANDARD) && DealType.isBuy(dealType) && CryptoCurrency.BITCOIN.equals(cryptoCurrency)) {
            deliveryType = DeliveryType.valueOf(update.getCallbackQuery().getData());
            responseSender.deleteCallbackMessageIfExists(update);
            if (DeliveryType.VIP.equals(deliveryType)) {
                BigDecimal fix = variablePropertiesReader.getBigDecimal(VariableType.FIX_COMMISSION_VIP,
                        dealPropertyService.getFiatCurrencyByPid(dealPid),
                        dealPropertyService.getDealTypeByPid(dealPid),
                        dealPropertyService.getCryptoCurrencyByPid(dealPid));
                modifyDealService.updateAmountByPid(dealPropertyService.getAmountByPid(dealPid).add(fix), dealPid);
            }
        } else {
            deliveryType = DeliveryType.STANDARD;
        }
        modifyDealService.updateDeliveryTypeByPid(readUserService.getCurrentDealByChatId(chatId),
                deliveryType);
    }

}
