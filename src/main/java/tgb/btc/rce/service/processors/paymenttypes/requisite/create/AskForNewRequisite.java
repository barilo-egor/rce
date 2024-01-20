package tgb.btc.rce.service.processors.paymenttypes.requisite.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.repository.bot.PaymentRequisiteRepository;
import tgb.btc.library.repository.bot.PaymentTypeRepository;
import tgb.btc.library.repository.bot.UserDataRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

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
        paymentRequisite.setOn(true);
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
