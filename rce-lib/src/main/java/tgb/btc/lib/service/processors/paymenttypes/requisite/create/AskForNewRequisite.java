package tgb.btc.lib.service.processors.paymenttypes.requisite.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.PaymentRequisite;
import tgb.btc.lib.bean.PaymentType;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.PaymentRequisiteRepository;
import tgb.btc.lib.repository.PaymentTypeRepository;
import tgb.btc.lib.repository.UserDataRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.util.Objects;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE_REQUISITE, step = 3)
public class AskForNewRequisite extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private UserDataRepository userDataRepository;

    private PaymentRequisiteRepository paymentRequisiteRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String requisite = UpdateUtil.getMessageText(update);
        PaymentRequisite paymentRequisite = new PaymentRequisite();
        paymentRequisite.setRequisite(requisite);
        PaymentType paymentType = paymentTypeRepository.getByPid(
                userDataRepository.getLongByUserPid(userRepository.getPidByChatId(chatId)));
        paymentRequisite.setPaymentType(paymentType);
        Integer count = paymentRequisiteRepository.countByPaymentTypePid(paymentType.getPid());
        if (Objects.isNull(count)) count = 0;
        paymentRequisite.setRequisiteOrder(count + 1);
        paymentRequisiteRepository.save(paymentRequisite);
        responseSender.sendMessage(chatId, "Реквизит сохранен.");
        processToAdminMainPanel(chatId);
    }

}
