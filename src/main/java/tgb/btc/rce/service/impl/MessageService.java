package tgb.btc.rce.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.service.bean.bot.UserService;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.library.vo.calculate.DealAmount;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.util.MessagePropertiesUtil;
import tgb.btc.rce.vo.InlineCalculatorVO;

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
        String cryptoCode = calculator.getCryptoCurrency().getShortName().toUpperCase();
        String fiatCode = calculator.getFiatCurrency().getCode().toUpperCase();
        String fiatFlag = calculator.getFiatCurrency().getFlag();
        if (Objects.isNull(dealAmount)) {
            return DealType.BUY.equals(dealType)
                    ? MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_BUY, cryptoCode, fiatCode,
                    StringUtils.EMPTY, fiatFlag,StringUtils.EMPTY, fiatFlag, StringUtils.EMPTY)
                    : MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_SELL, cryptoCode, fiatCode,
                    StringUtils.EMPTY, fiatFlag, StringUtils.EMPTY);
        } else {
            return DealType.BUY.equals(dealType)
                    ? MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_BUY, cryptoCode, fiatCode,
                    BigDecimalUtil.roundToPlainString(dealAmount.getCryptoAmount(), calculator.getCryptoCurrency().getScale()) + " " + cryptoCode,
                    fiatFlag, BigDecimalUtil.roundToPlainString(dealAmount.getAmount()) + " " + fiatCode, fiatFlag,
                    BigDecimalUtil.roundToPlainString(dealAmount.getAmountWithoutCommission()) + " " + fiatCode)
                    : MessagePropertiesUtil.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_SELL, cryptoCode, fiatCode,
                    BigDecimalUtil.roundToPlainString(dealAmount.getCryptoAmount(), calculator.getCryptoCurrency().getScale()) + " " + cryptoCode,
                    fiatFlag, BigDecimalUtil.roundToPlainString(dealAmount.getAmount()) + " " + fiatCode);
        }
    }

    public String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator) {
        return getInlineCalculatorMessage(dealType, calculator, null);
    }

}
