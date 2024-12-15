package tgb.btc.rce.service;

import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.vo.calculate.DealAmount;
import tgb.btc.rce.vo.InlineCalculatorVO;

public interface IMessageService {

    String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator, DealAmount dealAmount);

    String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator);
}
