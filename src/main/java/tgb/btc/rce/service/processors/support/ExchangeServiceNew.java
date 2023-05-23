package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.CalculatorQueryException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.*;
import tgb.btc.rce.util.BigDecimalUtil;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.CalculatorQuery;
import tgb.btc.rce.vo.DealAmount;
import tgb.btc.rce.vo.InlineButton;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ExchangeServiceNew {

    private KeyboardService keyboardService;

    private MessageService messageService;

    private DealRepository dealRepository;

    private UserRepository userRepository;

    private IResponseSender responseSender;

    private UserDiscountService userDiscountService;

    private CalculateService calculateService;

    private UserDiscountRepository userDiscountRepository;

    private InlineQueryCalculatorService queryCalculatorService;

    @Autowired
    public void setQueryCalculatorService(InlineQueryCalculatorService queryCalculatorService) {
        this.queryCalculatorService = queryCalculatorService;
    }

    @Autowired
    public void setUserDiscountRepository(UserDiscountRepository userDiscountRepository) {
        this.userDiscountRepository = userDiscountRepository;
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

    private final static Map<Long, BigDecimal> USERS_PERSONAL_SELL = new HashMap<>();

    private static final Map<Long, BigDecimal> USERS_PERSONAL_BUY = new HashMap<>();

    public static void putToUsersPersonalSell(Long userChatId, BigDecimal personalSell) {
        if (Objects.isNull(personalSell)) {
            throw new BaseException("Персональная скидка на продажу не может быть null.");
        }
        USERS_PERSONAL_SELL.put(userChatId, personalSell);
    }

    public static void putToUsersPersonalBuy(Long userChatId, BigDecimal personalBuy) {
        if (Objects.isNull(personalBuy)) {
            throw new BaseException("Персональная скидка на покупку не может быть null.");
        }
        USERS_PERSONAL_BUY.put(userChatId, personalBuy);
    }

    public void askForCurrency(Long chatId, DealType dealType) {
        messageService.sendMessageAndSaveMessageId(chatId, MessagePropertiesUtil.getChooseCurrency(dealType), keyboardService.getCurrencies(dealType));
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
        if (CryptoCurrency.BITCOIN.equals(currency)) propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM_CRYPTO_OR_FIAT; // TODO сейчас хардкод на "или в рублях", надо подставлять фиатное
        else propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM;
        String text = MessagePropertiesUtil.getMessage(propertiesMessage,
                dealRepository.getCryptoCurrencyByPid(userRepository.getCurrentDealByChatId(chatId)));

        messageService.sendMessageAndSaveMessageId(chatId, text, keyboardService.getCalculator(fiatCurrency, currency, dealType));
    }

    public boolean calculateDealAmount(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.getById(userRepository.getCurrentDealByChatId(chatId));
        DealAmount dealAmount = calculateService.calculate(UpdateUtil.getBigDecimalFromText(update),
                deal.getCryptoCurrency(), deal.getFiatCurrency(), deal.getDealType());
        dealAmount.updateDeal(deal);
        if (isLessThanMin(chatId, deal)) return false;
        userDiscountService.applyPersonal(chatId, deal);
        userDiscountService.applyBulk(deal);
        dealRepository.save(deal);
        return true;
    }

    private boolean isLessThanMin(Long chatId, Deal deal) {
        DealType dealType = deal.getDealType();
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        BigDecimal minSum = BotVariablePropertiesUtil.getBigDecimal(BotVariableType.MIN_SUM, dealType, cryptoCurrency);
        if (deal.getCryptoAmount().compareTo(minSum) < 0) {
            responseSender.sendMessage(chatId, "Минимальная сумма " + (DealType.isBuy(dealType) ? "покупки" : "продажи")
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

        DealAmount dealAmount = calculateService.calculate(calculatorQuery.getEnteredAmount(), calculatorQuery.getCurrency(),
                calculatorQuery.getFiatCurrency(), calculatorQuery.getDealType());
        BigDecimal totalAmount = dealAmount.getAmount();
        totalAmount = calculateService.calculateDiscount(calculatorQuery.getDealType(), totalAmount,
                userDiscountService.getPersonal(chatId, calculatorQuery.getDealType()));
        totalAmount = calculateService.calculateDiscount(calculatorQuery.getDealType(), totalAmount,
                userDiscountService.getBulk(totalAmount, calculatorQuery.getFiatCurrency()));
        String resultText = calculatorQuery.getDealType().getNominative() + ": "
                + BigDecimalUtil.round(dealAmount.getCryptoAmount(), calculatorQuery.getCurrency().getScale())
                + " ~ " + BigDecimalUtil.round(totalAmount, 0);
        responseSender.sendAnswerInlineQuery(inlineQueryId, resultText, "Нажмите сюда, чтобы отправить сумму.",
                BigDecimalUtil.toPlainString(calculatorQuery.getEnteredAmount()));
    }
}
