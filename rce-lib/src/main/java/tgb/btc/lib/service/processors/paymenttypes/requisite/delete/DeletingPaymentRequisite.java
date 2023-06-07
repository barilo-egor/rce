package tgb.btc.lib.service.processors.paymenttypes.requisite.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.BasePersist;
import tgb.btc.lib.bean.PaymentRequisite;
import tgb.btc.lib.bean.PaymentType;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.PaymentRequisiteRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<PaymentRequisite> paymentRequisites = paymentRequisiteRepository.getByPaymentType(paymentType).stream()
                .sorted(Comparator.comparing(BasePersist::getPid))
                .collect(Collectors.toList());
        for (int i = 0; i < paymentRequisites.size(); i++) {
            PaymentRequisite requisite = paymentRequisites.get(i);
            requisite.setRequisiteOrder(i + 1);
            paymentRequisiteRepository.save(requisite);
        }
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        showRequisitesForDelete.sendRequisites(UpdateUtil.getChatId(update), paymentType.getPid());
    }

}
