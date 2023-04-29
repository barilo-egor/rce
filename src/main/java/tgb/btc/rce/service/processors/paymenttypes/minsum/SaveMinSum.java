package tgb.btc.rce.service.processors.paymenttypes.minsum;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;

@CommandProcessor(command = Command.CHANGE_MIN_SUM, step = 3)
public class SaveMinSum extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private UserDataRepository userDataRepository;

    private UserRepository userRepository;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public SaveMinSum(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        Long minSum;
        try {
            minSum = Long.parseLong(UpdateUtil.getMessageText(update));
        } catch (Exception e) {
            responseSender.sendMessage(chatId, "Ошибка. Введите новую минимальную сумму.");
            return;
        }
        paymentTypeRepository.updateMinSumByPid(BigDecimal.valueOf(minSum),
                                                userDataRepository.getLongByUserPid(userRepository.getPidByChatId(chatId)));
        responseSender.sendMessage(chatId, "Минимальная сумма обновлена.");
        processToAdminMainPanel(chatId);
    }
}
