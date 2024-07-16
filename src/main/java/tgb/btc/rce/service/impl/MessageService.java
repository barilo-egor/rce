package tgb.btc.rce.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.util.BigDecimalUtil;
import tgb.btc.library.vo.calculate.DealAmount;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.service.IMessageService;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineCalculatorVO;

import java.util.Objects;

@Service
public class MessageService implements IMessageService {

    private IResponseSender responseSender;

    private IModifyUserService modifyUserService;

    private IMessagePropertiesService messagePropertiesService;

    @Autowired
    public void setMessagePropertiesService(IMessagePropertiesService messagePropertiesService) {
        this.messagePropertiesService = messagePropertiesService;
    }

    @Autowired
    public void setModifyUserService(IModifyUserService modifyUserService) {
        this.modifyUserService = modifyUserService;
    }

    @Autowired
    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }

    @Override
    public void sendMessageAndSaveMessageId(Long chatId, String text, ReplyKeyboard keyboard) {
        responseSender.sendMessage(chatId, text, keyboard)
                .ifPresent(message -> modifyUserService.updateBufferVariable(chatId, message.getMessageId().toString()));
    }

    @Override
    public void sendMessageAndSaveMessageId(SendMessage sendMessage) {
        responseSender.sendMessage(sendMessage)
                .ifPresent(message -> modifyUserService.updateBufferVariable(Long.parseLong(sendMessage.getChatId()), message.getMessageId().toString()));
    }

    @Override
    public String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator, DealAmount dealAmount) {
        String cryptoCode = calculator.getCryptoCurrency().getShortName().toUpperCase();
        String fiatCode = calculator.getFiatCurrency().getCode().toUpperCase();
        String fiatFlag = calculator.getFiatCurrency().getFlag();
        if (Objects.isNull(dealAmount)) {
            return DealType.BUY.equals(dealType)
                    ? messagePropertiesService.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_BUY, cryptoCode, fiatCode,
                    StringUtils.EMPTY, fiatFlag,StringUtils.EMPTY, fiatFlag, StringUtils.EMPTY)
                    : messagePropertiesService.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_SELL, cryptoCode, fiatCode,
                    StringUtils.EMPTY, fiatFlag, StringUtils.EMPTY);
        } else {
            String cryptoAmount = !calculator.getSwitched()
                    ? calculator.getSum()
                    : BigDecimalUtil.roundToPlainString(dealAmount.getCryptoAmount(), calculator.getCryptoCurrency().getScale());
            String amount = calculator.getSwitched()
                    ? calculator.getSum()
                    : BigDecimalUtil.roundToPlainString(dealAmount.getAmount());
            return DealType.BUY.equals(dealType)
                    ? messagePropertiesService.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_BUY, cryptoCode, fiatCode,
                    cryptoAmount + " " + cryptoCode,
                    fiatFlag, amount + " " + fiatCode, fiatFlag,
                    BigDecimalUtil.roundToPlainString(dealAmount.getCreditedAmount()) + " " + fiatCode)
                    : messagePropertiesService.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_SELL, cryptoCode, fiatCode,
                    cryptoAmount + " " + cryptoCode,
                    fiatFlag, amount + " " + fiatCode);
        }
    }

    @Override
    public String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator) {
        return getInlineCalculatorMessage(dealType, calculator, null);
    }

}
