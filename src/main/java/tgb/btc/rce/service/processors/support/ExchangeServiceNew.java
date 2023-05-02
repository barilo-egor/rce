package tgb.btc.rce.service.processors.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.rce.enums.CryptoCurrency;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.repository.DealRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.DealService;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.service.impl.MessageService;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.MessagePropertiesUtil;

@Service
public class ExchangeServiceNew {

    private IResponseSender responseSender;

    private UserService userService;

    private KeyboardService keyboardService;

    private DealService dealService;

    private MessageService messageService;

    private DealRepository dealRepository;

    @Autowired
    public void setDealRepository(DealRepository dealRepository) {
        this.dealRepository = dealRepository;
    }

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Autowired
    public void setDealService(DealService dealService) {
        this.dealService = dealService;
    }

    @Autowired
    public void setKeyboardService(KeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    public void askForCurrency(Long chatId, DealType dealType) {
        messageService.sendMessageAndSaveMessageId(chatId, MessagePropertiesUtil.getChooseCurrency(dealType), keyboardService.getCurrencies(dealType));
    }

    public void askForSum(Long chatId, CryptoCurrency currency, DealType dealType) {
        String text = MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM,
                                                       dealRepository.getCryptoCurrencyByChatIdOfCurrentDeal(chatId));
        messageService.sendMessageAndSaveMessageId(chatId, text, keyboardService.getCalculator(currency, dealType));
    }

}
