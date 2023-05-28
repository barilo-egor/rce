package tgb.btc.rce.service.processors.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.CalculatorQueryException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.util.*;
import tgb.btc.rce.vo.CalculatorQuery;
import tgb.btc.rce.vo.InlineButton;
import tgb.btc.rce.vo.calculate.DealAmount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExchangeServiceNew {

    private KeyboardService keyboardService;

    private MessageService messageService;

    private DealRepository dealRepository;

    private UserRepository userRepository;

    private IResponseSender responseSender;

    private UserDiscountService userDiscountService;

    private CalculateService calculateService;

    private InlineQueryCalculatorService queryCalculatorService;

    private BotMessageService botMessageService;

    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public void setBotMessageService(BotMessageService botMessageService) {
        this.botMessageService = botMessageService;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public void setQueryCalculatorService(InlineQueryCalculatorService queryCalculatorService) {
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

    public void askForCurrency(Long chatId, DealType dealType) {
        messageService.sendMessageAndSaveMessageId(chatId, MessagePropertiesUtil.getChooseCurrency(dealType),
                keyboardService.getCurrencies(dealType));
    }

    public boolean alreadyHasDeal(Long chatId) {
        if (dealRepository.getActiveDealsCountByUserChatId(chatId) > 0) {
            responseSender.sendMessage(chatId, "У вас уже есть активная заявка.",
                    InlineButton.buildData("Удалить", Command.DELETE_DEAL.getText()));
            return true;
        }
        return false;
    }

    public void askForSum(Long chatId, FiatCurrency fiatCurrency, CryptoCurrency currency, DealType dealType) {
        PropertiesMessage propertiesMessage;
        if (CryptoCurrency.BITCOIN.equals(currency)) {
            propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM_CRYPTO_OR_FIAT; // TODO сейчас хардкод на "или в рублях", надо подставлять фиатное
        } else {
            propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM;
        }
        String text = MessagePropertiesUtil.getMessage(propertiesMessage,
                dealRepository.getCryptoCurrencyByPid(
                        userRepository.getCurrentDealByChatId(chatId)));

        messageService.sendMessageAndSaveMessageId(chatId, text,
                keyboardService.getCalculator(fiatCurrency, currency, dealType));
    }

    public boolean calculateDealAmount(Long chatId, BigDecimal enteredAmount) {
        Deal deal = dealRepository.getById(userRepository.getCurrentDealByChatId(chatId));
        DealAmount dealAmount = calculateService.calculate(enteredAmount,
                deal.getCryptoCurrency(), deal.getFiatCurrency(),
                deal.getDealType());
        if (isLessThanMin(chatId, deal)) return false;
        userDiscountService.applyPersonal(chatId, deal.getDealType(), dealAmount);
        userDiscountService.applyBulk(deal.getFiatCurrency(), deal.getDealType(), dealAmount);
        dealAmount.updateDeal(deal);
        dealRepository.save(deal);
        return true;
    }

    private boolean isLessThanMin(Long chatId, Deal deal) {
        DealType dealType = deal.getDealType();
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        BigDecimal minSum = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.MIN_SUM, dealType, cryptoCurrency);
        if (deal.getCryptoAmount().compareTo(minSum) < 0) {
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
            responseSender.sendAnswerInlineQuery(inlineQueryId, "Введите сумму.");
            return;
        }

        DealAmount dealAmount = calculateService.calculate(calculatorQuery.getEnteredAmount(),
                calculatorQuery.getCurrency(),
                calculatorQuery.getFiatCurrency(),
                calculatorQuery.getDealType());
        userDiscountService.applyPersonal(chatId, calculatorQuery.getDealType(), dealAmount);
        userDiscountService.applyBulk(calculatorQuery.getFiatCurrency(), calculatorQuery.getDealType(), dealAmount);

        String resultText = calculatorQuery.getDealType().getNominative() + ": "
                + BigDecimalUtil.round(dealAmount.getCryptoAmount(), calculatorQuery.getCurrency().getScale())
                + " ~ " + BigDecimalUtil.round(dealAmount.getAmount(), 0);
        responseSender.sendAnswerInlineQuery(inlineQueryId, resultText, "Нажмите сюда, чтобы отправить сумму.",
                BigDecimalUtil.toPlainString(calculatorQuery.getEnteredAmount()));
    }

    public void askForUserRequisites(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.getById(userRepository.getCurrentDealByChatId(chatId));
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        ReplyKeyboard keyboard;
        String message;
        if (DealType.isBuy(deal.getDealType())) {
            message = "\uD83D\uDCDDВведите " + cryptoCurrency.getDisplayName()
                    + "-адрес кошелька, куда вы хотите отправить "
                    + BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale())
                    + " " + cryptoCurrency.getShortName();
            List<InlineButton> buttons = new ArrayList<>();

            if (dealRepository.getPassedDealsCountByUserChatIdAndDealTypeAndCryptoCurrency(chatId, DealType.BUY, cryptoCurrency) > 0) {
                String wallet = dealRepository.getWalletFromLastPassedByChatIdAndDealTypeAndCryptoCurrency(chatId, DealType.BUY, cryptoCurrency);
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
                .ifPresent(sentMessage -> userRepository.updateBufferVariable(chatId, sentMessage.getMessageId().toString()));
    }

    public void askForUserPromoCode(Long chatId) {
        Deal deal = dealRepository.getById(userRepository.getCurrentDealByChatId(chatId));
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        BigDecimal dealAmount = deal.getAmount();
        BigDecimal promoCodeDiscount = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.PROMO_CODE_DISCOUNT.getKey());
        BigDecimal discount = BigDecimalUtil.multiplyHalfUp(deal.getCommission(), calculateService.getPercentsFactor(promoCodeDiscount));
        dealRepository.updateDiscountByPid(discount, deal.getPid());
        BigDecimal sumWithDiscount = dealAmount.subtract(discount);
        String sumWithDiscountString = BigDecimalUtil.roundToPlainString(sumWithDiscount);

        String message = "<b>Покупка " + cryptoCurrency.getDisplayName() + "</b>: "
                + BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(), cryptoCurrency.getScale()) + "\n\n"
                + "<b>Сумма перевода</b>: <s>" + BigDecimalUtil.roundToPlainString(dealAmount)
                + "</s> " + sumWithDiscountString + "\n\n"
                + "\uD83C\uDFAB У вас есть промокод: <b>" + BotVariablePropertiesUtil.getVariable(BotVariableType.PROMO_CODE_NAME)
                + "</b>, который даёт скидку в размере " + BigDecimalUtil.roundToPlainString(promoCodeDiscount) + "% от комиссии"
                + "\n\n"
                + "✅ <b>Использовать промокод</b> как скидку?";
        ReplyKeyboard keyboard = KeyboardUtil.buildInline(List.of(
                InlineButton.builder()
                        .text("Использовать, " + sumWithDiscountString)
                        .data(BotStringConstants.USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                InlineButton.builder()
                        .text("Без промокода, " + dealAmount)
                        .data(BotStringConstants.DONT_USE_PROMO)
                        .inlineType(InlineType.CALLBACK_DATA)
                        .build(),
                KeyboardUtil.INLINE_BACK_BUTTON
        ));

        responseSender.sendMessage(chatId, message, keyboard, "HTML");
    }

    public void processPromoCode(Update update) {
        if (!update.hasCallbackQuery() || !isUsePromoData(update.getCallbackQuery().getData()))
            throw new BaseException("Ожидался CallbackQuery с USE_PROMO.");
        dealRepository.updateIsUsedPromoByPid(true,
                userRepository.getCurrentDealByChatId(UpdateUtil.getChatId(update)));
    }

    private boolean isUsePromoData(String data) {
        return BotStringConstants.USE_PROMO.equals(data);
    }

    public void saveRequisites(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userRepository.getCurrentDealByChatId(UpdateUtil.getChatId(update));
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        String wallet;
        if (DealType.isBuy(dealType)) {
            if (update.hasMessage()) {
                wallet = UpdateUtil.getMessageText(update);
                validateWallet(wallet);
            } else wallet = dealRepository.getWalletFromLastPassedByChatIdAndDealTypeAndCryptoCurrency(
                    chatId, dealType, dealRepository.getCryptoCurrencyByPid(currentDealPid));
        } else wallet = UpdateUtil.getMessageText(update);
        dealRepository.updateWalletByPid(wallet, currentDealPid);
    }


    private void validateWallet(String wallet) {
        if (wallet.length() < 33) {
            throw new BaseException("Длина кошелька не может быть меньше 33-х символов.");
        }
    }

    public void askForPaymentType(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.getById(userRepository.getCurrentDealByChatId(chatId));
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
                .append(BigDecimalUtil.roundToPlainString(deal.getCryptoAmount(), deal.getCryptoCurrency().getScale()));
        if (DealType.isBuy(deal.getDealType())) {
            dealAmount = userDiscountService.applyDealDiscounts(deal);
            messageNew.append("\n" + "<b>").append(displayCurrencyName).append("-адрес</b>:")
                    .append("<code>").append(deal.getWallet()).append("</code>").append("\n\n")
                    .append("\uD83D\uDCB5<b>Сумма перевода</b>: ")
                    .append(BigDecimalUtil.toPlainString(dealAmount))
                    .append(" ").append(deal.getFiatCurrency().getDisplayName()).append("\n\n")
                    .append(additionalText).append("<b>Выберите способ оплаты:</b>");
        } else messageNew.append("\n\n" + "\uD83D\uDCB5<b>Сумма перевода</b>: ")
                .append(BigDecimalUtil.roundToPlainString(dealAmount)).append(" ")
                .append(deal.getFiatCurrency().getDisplayName()).append("\n\n")
                .append(additionalText).append("<b>Выберите способ получения перевода:</b>");

        responseSender.sendMessage(chatId, messageNew.toString(),
                keyboardService.getPaymentTypes(deal.getDealType(), deal.getFiatCurrency()), "HTML");
    }

    public Boolean savePaymentTypeNew(Update update) {
        if (!update.hasCallbackQuery()) {
            return false;
        }
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        DealType dealType = dealRepository.getDealTypeByPid(currentDealPid);
        PaymentType paymentType = paymentTypeRepository.getByPid(Long.parseLong(update.getCallbackQuery().getData()));
        if (paymentType.getMinSum().compareTo(dealRepository.getAmountByPid(currentDealPid)) > 0) {
            responseSender.sendMessage(chatId, "Минимальная сумма для " + dealType.getGenitive() + " через "
                    + paymentType.getName() + " равна " + paymentType.getMinSum().toPlainString());
            userRepository.updateStepByChatId(chatId, 2);
            askForSum(chatId, dealRepository.getFiatCurrencyByPid(currentDealPid),
                    dealRepository.getCryptoCurrencyByPid(currentDealPid), DealType.BUY);
            return null;
        }
        dealRepository.updatePaymentTypeByPid(paymentType, currentDealPid);
        return true;
    }
}
