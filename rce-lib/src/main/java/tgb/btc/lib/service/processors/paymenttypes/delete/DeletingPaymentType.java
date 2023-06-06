package tgb.btc.lib.service.processors.paymenttypes.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.PaymentType;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.DealType;
import tgb.btc.lib.repository.PaymentRequisiteRepository;
import tgb.btc.lib.repository.PaymentTypeRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.DELETING_PAYMENT_TYPE)
public class DeletingPaymentType extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private ShowPaymentTypesForDelete showPaymentTypesForDelete;

    private PaymentRequisiteRepository paymentRequisiteRepository;

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
    }

    @Autowired
    public void setShowPaymentTypesForDelete(ShowPaymentTypesForDelete showPaymentTypesForDelete) {
        this.showPaymentTypesForDelete = showPaymentTypesForDelete;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long pid = Long.valueOf(values[1]);
        PaymentType paymentType = paymentTypeRepository.getByPid(pid);
        DealType dealType = paymentType.getDealType();
        paymentRequisiteRepository.deleteByPaymentTypePid(paymentType.getPid());
        paymentTypeRepository.deleteById(pid);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        String message = "Тип оплаты на " + paymentType.getDealType().getAccusative() + " \"" + paymentType.getName() + "\" удален.";
        responseSender.sendMessage(UpdateUtil.getChatId(update), message);
        showPaymentTypesForDelete.sendPaymentTypes(chatId, dealType, paymentType.getFiatCurrency());
    }
}
