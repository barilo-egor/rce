package tgb.btc.rce.service.processors.paymenttypes.requisite.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETING_PAYMENT_TYPE_REQUISITE)
public class DeletingPaymentRequisite extends Processor {

    private ShowRequisitesForDelete showRequisitesForDelete;

    private IPaymentRequisiteService paymentRequisiteService;

    @Autowired
    public void setPaymentRequisiteService(IPaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Autowired
    public void setShowRequisitesForDelete(ShowRequisitesForDelete showRequisitesForDelete) {
        this.showRequisitesForDelete = showRequisitesForDelete;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) {
            return;
        }
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);

        PaymentRequisite paymentRequisite = paymentRequisiteService.findById(Long.parseLong(values[1]));
        PaymentType paymentType = paymentRequisiteService.getPaymentTypeByPid(paymentRequisite.getPid());

        paymentRequisiteService.delete(paymentRequisite);
        responseSender.deleteMessage(UpdateUtil.getChatId(update), update.getCallbackQuery().getMessage().getMessageId());
        showRequisitesForDelete.sendRequisites(UpdateUtil.getChatId(update), paymentType.getPid());
    }

}
