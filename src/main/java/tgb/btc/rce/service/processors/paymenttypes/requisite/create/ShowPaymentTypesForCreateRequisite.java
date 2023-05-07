package tgb.btc.rce.service.processors.paymenttypes.requisite.create;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE_REQUISITE, step = 1)
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

    @Autowired
    public ShowPaymentTypesForCreateRequisite(IResponseSender responseSender, UserService userService) {
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
        userDataRepository.updateLongByUserPid(userRepository.getPidByChatId(chatId), Long.parseLong(values[1]));
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        userService.nextStep(chatId);
        responseSender.sendMessage(chatId, "Введите реквизит.");
    }

}
