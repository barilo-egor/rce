package tgb.btc.lib.service.processors.paymenttypes.minsum;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.PaymentTypeRepository;
import tgb.btc.lib.repository.UserDataRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.math.BigDecimal;

@CommandProcessor(command = Command.CHANGE_MIN_SUM, step = 4)
public class SaveMinSum extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private UserDataRepository userDataRepository;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        long minSum;
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
