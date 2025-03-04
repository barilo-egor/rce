package tgb.btc.rce.service.impl.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.CryptoCurrency;
import tgb.btc.library.interfaces.enums.MessageImage;
import tgb.btc.library.interfaces.service.bean.bot.deal.read.IDealPropertyService;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.library.interfaces.service.design.IMessageImageService;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.sender.IMessageImageResponseSender;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.ICalculatorTypeService;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.IUpdateService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.IMessagePropertiesService;

public abstract class SimpleCalculatorService implements ICalculatorTypeService {

    protected IReadUserService readUserService;

    protected IModifyUserService modifyUserService;

    protected IDealPropertyService dealPropertyService;

    protected IResponseSender responseSender;

    protected IKeyboardService keyboardService;

    protected IKeyboardBuildService keyboardBuildService;

    private IMessagePropertiesService messagePropertiesService;

    private IUpdateService updateService;

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

        MessageImage messageImage;
        if (CryptoCurrency.BITCOIN.equals(cryptoCurrency)) {
            messageImage = MessageImage.INPUT_BITCOIN_DEAL_SUM;
        } else {
            messageImage = MessageImage.INPUT_DEAL_SUM;
        }
        String text = messageImageService.getMessage(messageImage).formatted(cryptoCurrency.name());

        setState(chatId);
        messageImageResponseSender.sendMessage(messageImage, chatId, text, getKeyboard(chatId));
    }

    public abstract ReplyKeyboard getKeyboard(Long chatId);

    public abstract void setState(Long chatId);
}
