package tgb.btc.rce.service.impl.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.service.util.IUpdateDispatcher;

public abstract class SimpleCalculatorService implements ICalculatorTypeService {

    protected IReadUserService readUserService;

    protected IModifyUserService modifyUserService;

    protected IDealPropertyService dealPropertyService;

    protected IResponseSender responseSender;

    protected IKeyboardService keyboardService;

    protected IUpdateDispatcher updateDispatcher;

    protected IKeyboardBuildService keyboardBuildService;

    private IMessagePropertiesService messagePropertiesService;

    private IUpdateService updateService;

    @Autowired
    public void setUpdateService(IUpdateService updateService) {
        this.updateService = updateService;
    }

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setKeyboardBuildService(IKeyboardBuildService keyboardBuildService) {
        this.keyboardBuildService = keyboardBuildService;
    }

    @Autowired
    public void setResponseSender(IResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Autowired
    public void setKeyboardService(IKeyboardService keyboardService) {
        this.keyboardService = keyboardService;
    }

    @Autowired
    public void setUpdateDispatcher(IUpdateDispatcher updateDispatcher) {
        this.updateDispatcher = updateDispatcher;
    }

    @Autowired
    public void setDealPropertyService(IDealPropertyService dealPropertyService) {
        this.dealPropertyService = dealPropertyService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    @Autowired
    public void setReadUserService(IReadUserService readUserService) {
        this.readUserService = readUserService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        Long currentDealPid = readUserService.getCurrentDealByChatId(chatId);
        CryptoCurrency cryptoCurrency = dealPropertyService.getCryptoCurrencyByPid(currentDealPid);
        PropertiesMessage propertiesMessage;
        if (CryptoCurrency.BITCOIN.equals(cryptoCurrency))
            propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM_CRYPTO_OR_FIAT; // TODO сейчас хардкод на "или в рублях", надо подставлять фиатное
        else propertiesMessage = PropertiesMessage.DEAL_INPUT_SUM;
        String text = messagePropertiesService.getMessage(propertiesMessage,
                dealPropertyService.getCryptoCurrencyByPid(
                        readUserService.getCurrentDealByChatId(chatId)));
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
