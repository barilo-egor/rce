package tgb.btc.rce.service.processors.paymenttypes.requisite.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.repository.bot.PaymentRequisiteRepository;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETING_PAYMENT_TYPE_REQUISITE)
public class DeletingPaymentRequisite extends Processor {

    private ShowRequisitesForDelete showRequisitesForDelete;

    private PaymentRequisiteRepository paymentRequisiteRepository;

    @Autowired
    public void setShowRequisitesForDelete(ShowRequisitesForDelete showRequisitesForDelete) {
        this.showRequisitesForDelete = showRequisitesForDelete;
    }

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) {
            return;
        }
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);

        PaymentRequisite paymentRequisite = paymentRequisiteRepository.getById(Long.parseLong(values[1]));
        PaymentType paymentType = paymentRequisiteRepository.getPaymentTypeByPid(paymentRequisite.getPid());

        paymentRequisiteRepository.delete(paymentRequisite);
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        showRequisitesForDelete.sendRequisites(UpdateUtil.getChatId(update), paymentType.getPid());
    }

}
