package tgb.btc.lib.service.processors.paymenttypes.requisite.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.UserDataRepository;
import tgb.btc.lib.repository.UserRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE_REQUISITE, step = 2)
@Slf4j
public class ShowPaymentTypesForCreateRequisite extends Processor {

    private UserDataRepository userDataRepository;

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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
        userDataRepository.updateLongByUserPid(userRepository.getPidByChatId(chatId), Long.parseLong(values[1]));
        log.info("Пид типа оплаты=" + Long.parseLong(values[1]));
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        userService.nextStep(chatId);
        responseSender.sendMessage(chatId, "Введите реквизит.");
    }

}
