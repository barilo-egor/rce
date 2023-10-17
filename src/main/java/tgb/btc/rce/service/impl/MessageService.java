package tgb.btc.rce.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.impl.bean.UserService;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.vo.InlineCalculatorVO;
import tgb.btc.rce.vo.calculate.DealAmount;

import java.util.Objects;

@Service
public class MessageService {

    private IResponseSender responseSender;

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    public void sendMessageAndSaveMessageId(Long chatId, String text, ReplyKeyboard keyboard) {
        responseSender.sendMessage(chatId, text, keyboard)
                .ifPresent(message -> userService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }

    public void sendMessageAndSaveMessageId(SendMessage sendMessage) {
        responseSender.sendMessage(sendMessage)
                .ifPresent(message -> userService.updateBufferVariable(Long.parseLong(sendMessage.getChatId()), message.getMessageId().toString()));
    }

    public String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator, DealAmount dealAmount) {
        if (Objects.isNull(dealAmount)) {
            return DealType.BUY.equals(dealType)
                    ? MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_BUY,
                    calculator.getCryptoCurrency().getShortName().toUpperCase(),
                    calculator.getFiatCurrency().getCode().toUpperCase(), StringUtils.EMPTY,
                    calculator.getFiatCurrency().getFlag(),StringUtils.EMPTY,
                    calculator.getFiatCurrency().getFlag(), StringUtils.EMPTY)
                    : MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_SELL,
                    calculator.getCryptoCurrency().getShortName().toUpperCase(),
                    calculator.getFiatCurrency().getCode().toUpperCase(), StringUtils.EMPTY,
                    calculator.getFiatCurrency().getFlag(), StringUtils.EMPTY);
        } else {
            return DealType.BUY.equals(dealType)
                    ? MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_BUY,
                    calculator.getCryptoCurrency().getShortName().toUpperCase(),
                    calculator.getFiatCurrency().getCode().toUpperCase(), dealAmount.getCryptoAmount(),
                    calculator.getFiatCurrency().getFlag(), dealAmount.getAmount(),
                    calculator.getFiatCurrency().getFlag(), dealAmount.getAmountWithoutCommission())
                    : MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_SELL,
                    calculator.getCryptoCurrency().getShortName().toUpperCase(),
                    calculator.getFiatCurrency().getCode().toUpperCase(),
                    dealAmount.getCryptoAmount(), calculator.getFiatCurrency().getFlag(), dealAmount.getAmount());
        }
    }

    public String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator) {
        return getInlineCalculatorMessage(dealType, calculator, null);
    }

}
