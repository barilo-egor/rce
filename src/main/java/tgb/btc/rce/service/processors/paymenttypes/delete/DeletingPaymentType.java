package tgb.btc.rce.service.processors.paymenttypes.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.repository.bot.PaymentRequisiteRepository;
import tgb.btc.library.repository.bot.PaymentTypeRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

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
