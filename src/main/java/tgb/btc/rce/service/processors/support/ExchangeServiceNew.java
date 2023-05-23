package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.*;
import tgb.btc.rce.exception.BaseException;
import tgb.btc.rce.exception.EnumTypeNotFoundException;
import tgb.btc.rce.exception.NumberParseException;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserDiscountRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.IUserDiscountService;
import tgb.btc.rce.service.impl.CalculateService;
import tgb.btc.rce.service.impl.InlineQueryCalculatorService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.MessageService;
import tgb.btc.rce.util.*;
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

    private IUserDiscountService userDiscountService;

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
    public void setUserDiscountService(IUserDiscountService userDiscountService) {
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

    public void askForSum(Long chatId, CryptoCurrency currency, DealType dealType) {
        PropertiesMessage propertiesMessage;
        if (CryptoCurrency.BITCOIN.equals(currency)) propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM_CRYPTO_OR_FIAT; // TODO сейчас хардкод на "или в рублях", надо подставлять фиатное
        else propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM;
        String text = MessagePropertiesUtil.getMessage(propertiesMessage,
                dealRepository.getCryptoCurrencyByPid(userRepository.getCurrentDealByChatId(chatId)));

        messageService.sendMessageAndSaveMessageId(chatId, text, keyboardService.getCalculator(currency, dealType));
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
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        String query = queryCalculatorService.getQueryWithPoints(update);
        BigDecimal enteredAmount;
        CryptoCurrency currency = null;

        if (queryCalculatorService.hasEnteredAmount(query)) {
            askForCryptoSum(update);
            return;
        }

        try {
            currency = CryptoCurrency.fromShortName(query.substring(0, query.indexOf("-")));
            enteredAmount = BigDecimal.valueOf(NumberUtil.getInputDouble(query.substring(query.indexOf(" ") + 1)));
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

        CryptoCurrency cryptoCurrency = dealRepository.getCryptoCurrencyByPid(currentDealPid);
        BigDecimal minSum = BigDecimalUtil.round(
                BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM, DealType.SELL, cryptoCurrency),
                cryptoCurrency.getScale()
        );

        if (enteredAmount.doubleValue() < minSum.doubleValue()) {
            sendInlineAnswer(update, "Минимальная сумма продажи " + cryptoCurrency.getDisplayName()
                    + " = " + minSum.stripTrailingZeros().toPlainString() + ".", false);
            return;
        }
        enteredAmount = BigDecimal.valueOf(BigDecimalUtil.round(enteredAmount, cryptoCurrency.getScale()).doubleValue());
        BigDecimal roundedConvertedSum =
                calculateService.calculate(
                        enteredAmount, cryptoCurrency, dealRepository.getFiatCurrencyByPid(currentDealPid), dealRepository.getDealTypeByPid(currentDealPid)
                ).getAmount();
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
        sendInlineAnswer(update, enteredAmount.stripTrailingZeros().toPlainString() + " " + currency.getDisplayName() + " ~ " +
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
}
