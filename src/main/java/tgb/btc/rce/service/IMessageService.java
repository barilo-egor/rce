package tgb.btc.rce.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.vo.calculate.DealAmount;
import tgb.btc.rce.vo.InlineCalculatorVO;

public interface IMessageService {

    void sendMessageAndSaveMessageId(Long chatId, String text, ReplyKeyboard keyboard);

    void sendMessageAndSaveMessageId(SendMessage sendMessage);

    String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator, DealAmount dealAmount);

    String getInlineCalculatorMessage(DealType dealType, InlineCalculatorVO calculator);
}
