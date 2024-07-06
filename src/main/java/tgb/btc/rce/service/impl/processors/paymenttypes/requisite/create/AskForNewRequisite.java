package tgb.btc.rce.service.impl.processors.paymenttypes.requisite.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.service.bean.bot.IUserDataService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE_REQUISITE, step = 3)
public class AskForNewRequisite extends Processor {

    private IPaymentTypeService paymentTypeService;

    private IUserDataService userDataService;

    private IPaymentRequisiteService paymentRequisiteService;

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Autowired
    public void setUserDataService(IUserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @Autowired
    public void setPaymentRequisiteService(IPaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String requisite = UpdateUtil.getMessageText(update);
        PaymentRequisite paymentRequisite = new PaymentRequisite();
        paymentRequisite.setOn(true);
        paymentRequisite.setRequisite(requisite);
        PaymentType paymentType = paymentTypeService.getByPid(
                userDataService.getLongByUserPid(readUserService.getPidByChatId(chatId)));
        paymentRequisite.setPaymentType(paymentType);
        paymentRequisiteService.save(paymentRequisite);
        responseSender.sendMessage(chatId, "Реквизит сохранен.");
        processToAdminMainPanel(chatId);
    }

}
