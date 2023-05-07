package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.bean.Deal;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.MessageService;
import tgb.btc.rce.util.BotVariablePropertiesUtil;
import tgb.btc.rce.util.ConverterUtil;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;

@Service
public class ExchangeServiceNew {

    private KeyboardService keyboardService;

    private MessageService messageService;

    private DealRepository dealRepository;

    private UserRepository userRepository;

    private IResponseSender responseSender;

    private PersonalDiscountsCache personalDiscountsCache;

    @Autowired
    public void setPersonalDiscountsCache(PersonalDiscountsCache personalDiscountsCache) {
        this.personalDiscountsCache = personalDiscountsCache;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        String text = MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM,
                                                       dealRepository.getCryptoCurrencyByChatIdOfCurrentDeal(chatId));
        messageService.sendMessageAndSaveMessageId(chatId, text, keyboardService.getCalculator(currency, dealType));
    }

    public boolean saveSum(Update update, DealType dealType) {
        Long chatId = UpdateUtil.getChatId(update);
        Deal deal = dealRepository.getById(userRepository.getCurrentDealByChatId(chatId));
        CryptoCurrency cryptoCurrency = deal.getCryptoCurrency();
        Double minSum = BotVariablePropertiesUtil.getMinSumBuy(cryptoCurrency);
        Double sum = UpdateUtil.getDoubleFromText(update);

        if (sum < minSum) {
            responseSender.sendMessage(chatId, "Минимальная сумма покупки " + cryptoCurrency.getDisplayName()
                    + " = " + BigDecimal.valueOf(minSum).stripTrailingZeros().toPlainString() + ".");
            return false;
        }

        deal.setCryptoAmount(BigDecimal.valueOf(sum));
        BigDecimal amount = ConverterUtil.convertCryptoToRub(cryptoCurrency, sum, dealType);
        BigDecimal personalDiscount = personalDiscountsCache.getDiscount(chatId, dealType);
        return false;
    }
}
