package tgb.btc.lib.service.processors.paymenttypes.turning;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.BotKeyboard;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.FiatCurrency;
import tgb.btc.lib.repository.UserDataRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.TURN_PAYMENT_TYPES, step = 1)
public class FiatCurrencyTurnPaymentType extends Processor {

    private UserDataRepository userDataRepository;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        userDataRepository.updateFiatCurrencyByUserChatId(chatId, FiatCurrency.getByCode(UpdateUtil.getMessageText(update)));
        responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL, BotKeyboard.BUY_OR_SELL);
        userService.nextStep(chatId, Command.TURN_PAYMENT_TYPES);
    }
}
