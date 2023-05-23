package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.BotVariableType;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.IUserDiscountService;
import tgb.btc.rce.service.impl.CalculateService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.MessageService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.DealAmount;

import java.math.BigDecimal;

@Service
public class ExchangeServiceNew {

    private KeyboardService keyboardService;

    private MessageService messageService;

    private DealRepository dealRepository;

    private UserRepository userRepository;

    private IResponseSender responseSender;

    private IUserDiscountService userDiscountService;

    private CalculateService calculateService;

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

    public void askForCurrency(Long chatId, DealType dealType) {
        messageService.sendMessageAndSaveMessageId(chatId, MessagePropertiesUtil.getChooseCurrency(dealType), keyboardService.getCurrencies(dealType));
    }

    public void askForSum(Long chatId, CryptoCurrency currency, DealType dealType) {
        PropertiesMessage propertiesMessage;
        if (CryptoCurrency.BITCOIN.equals(currency)) propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM_CRYPTO_OR_FIAT; // TODO сейчас хардкод на "или в рублях", надо подставлять фиатное
        else propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM;
        String text = MessagePropertiesUtil.getMessage(propertiesMessage,
                dealRepository.getCryptoCurrencyByPid(userRepository.getCurrentDealByChatId(chatId)));

        messageService.sendMessageAndSaveMessageId(chatId, text, keyboardService.getCalculator(currency, dealType));
    }

    public boolean saveSum(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.getById(userRepository.getCurrentDealByChatId(chatId));
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        DealType dealType = deal.getDealType();
        DealAmount dealAmount = calculateService.convertNew(
                cryptoCurrency, UpdateUtil.getBigDecimalFromText(update), deal.getFiatCurrency(), dealType
        );
        if (isLessThanMin(chatId, deal, dealAmount.getCryptoAmount())) return false;
        dealAmount.updateDeal(deal);
        userDiscountService.applyPersonal(chatId, deal);
        userDiscountService.applyBulk(deal);
        dealRepository.save(deal);
        return true;
    }

    private boolean isLessThanMin(Long chatId, Deal deal, BigDecimal cryptoAmount) {
        DealType dealType = deal.getDealType();
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        double minSum = BotVariablePropertiesUtil.getDouble(BotVariableType.MIN_SUM, dealType, cryptoCurrency);
        if (cryptoAmount.doubleValue() < minSum) {
            String dealTypeString = DealType.isBuy(dealType) ? "покупки" : "продажи";
            responseSender.sendMessage(chatId, "Минимальная сумма " + dealTypeString + " " + cryptoCurrency.getDisplayName()
                    + " = " + BigDecimal.valueOf(minSum).stripTrailingZeros().toPlainString() + ".");
            return true;
        }
        return false;
    }

    private boolean isEnteredInCrypto(CryptoCurrency cryptoCurrency, Double enteredAmount) {
        return !CryptoCurrency.BITCOIN.equals(cryptoCurrency)
                || enteredAmount < BotVariablePropertiesUtil.getDouble(BotVariableType.DEAL_BTC_MAX_ENTERED_SUM.getKey());
    }
}
