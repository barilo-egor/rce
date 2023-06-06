package tgb.btc.lib.service.processors.paymenttypes.minsum;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.UserDataRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;


@CommandProcessor(command = Command.CHANGE_MIN_SUM, step = 3)
public class AskForMinSum extends Processor {

    private UserDataRepository userDataRepository;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выберите тип оплаты.");
            return;
        }
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long paymentTypePid = Long.parseLong(values[1]);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        userDataRepository.updateLongByUserPid(userRepository.getPidByChatId(chatId), paymentTypePid);
        responseSender.sendMessage(chatId, "Введите минимальную сумму в рублях.");
        userService.nextStep(chatId);
    }
}
