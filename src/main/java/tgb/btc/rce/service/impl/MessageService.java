package tgb.btc.rce.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.util.IBigDecimalService;
import tgb.btc.library.vo.calculate.DealAmount;
import tgb.btc.rce.enums.PropertiesMessage;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.sender.ResponseSender;
import tgb.btc.rce.service.IMessageService;
import tgb.btc.rce.service.util.IMessagePropertiesService;
import tgb.btc.rce.vo.InlineCalculatorVO;

import java.util.Objects;

@Service
public class MessageService implements IMessageService {

    private IResponseSender responseSender;

    private IModifyUserService modifyUserService;

    private IMessagePropertiesService messagePropertiesService;

    private IBigDecimalService bigDecimalService;

    @Autowired
    public void setBigDecimalService(IBigDecimalService bigDecimalService) {
        this.bigDecimalService = bigDecimalService;
    }

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
                    : bigDecimalService.roundToPlainString(dealAmount.getCryptoAmount(), calculator.getCryptoCurrency().getScale());
            String amount = calculator.getSwitched()
                    ? calculator.getSum()
                    : bigDecimalService.roundToPlainString(dealAmount.getAmount());
            return DealType.BUY.equals(dealType)
                    ? messagePropertiesService.getMessage(PropertiesMessage.DEAL_INPUT_SUM_TO_BUY, cryptoCode, fiatCode,
                    cryptoAmount + " " + cryptoCode,
                    fiatFlag, amount + " " + fiatCode, fiatFlag,
                    bigDecimalService.roundToPlainString(dealAmount.getCreditedAmount()) + " " + fiatCode)
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
