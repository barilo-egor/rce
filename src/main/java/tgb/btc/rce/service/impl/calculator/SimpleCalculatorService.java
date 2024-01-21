package tgb.btc.rce.service.impl.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.library.repository.bot.DealRepository;
import tgb.btc.library.repository.bot.UserRepository;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.IUpdateDispatcher;
import tgb.btc.rce.service.impl.KeyboardService;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.util.UpdateUtil;

@Service
public abstract class SimpleCalculatorService implements ICalculatorTypeService {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected DealRepository dealRepository;

    @Autowired
    protected IResponseSender responseSender;

    @Autowired
    protected KeyboardService keyboardService;

    @Autowired
    protected IUpdateDispatcher updateDispatcher;

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long currentDealPid = userRepository.getCurrentDealByChatId(chatId);
        CryptoCurrency cryptoCurrency = dealRepository.getCryptoCurrencyByPid(currentDealPid);
        PropertiesMessage propertiesMessage;
        if (CryptoCurrency.BITCOIN.equals(cryptoCurrency))
            propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM_CRYPTO_OR_FIAT; // TODO сейчас хардкод на "или в рублях", надо подставлять фиатное
        else propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM;
        String text = MessagePropertiesUtil.getMessage(propertiesMessage,
                                                       dealRepository.getCryptoCurrencyByPid(
                                                               userRepository.getCurrentDealByChatId(chatId)));
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");
        addKeyboard(sendMessage);
        setCommand(chatId);
        responseSender.sendMessage(sendMessage);
    }

    public abstract void addKeyboard(SendMessage sendMessage);

    public abstract void setCommand(Long chatId);
}
