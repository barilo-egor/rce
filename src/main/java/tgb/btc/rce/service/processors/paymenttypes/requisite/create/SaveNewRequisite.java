package tgb.btc.rce.service.processors.paymenttypes.requisite.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentRequisite;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.PaymentRequisiteRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.repository.UserRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE_REQUISITE, step = 3)
public class SaveNewRequisite extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private PaymentRequisiteRepository paymentRequisiteRepository;

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
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    @Autowired
    public SaveNewRequisite(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
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
        paymentRequisite.setOrder(paymentRequisiteRepository);
        paymentRequisiteRepository.save(paymentRequisite);
        responseSender.sendMessage(chatId, "Реквизит сохранен.");
        processToAdminMainPanel(chatId);
    }

}
