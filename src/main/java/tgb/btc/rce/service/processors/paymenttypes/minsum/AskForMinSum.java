package tgb.btc.rce.service.processors.paymenttypes.minsum;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.CHANGE_MIN_SUM, step = 2)
public class AskForMinSum extends Processor {

    private UserRepository userRepository;

    private UserDataRepository userDataRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Autowired
    public AskForMinSum(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
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
