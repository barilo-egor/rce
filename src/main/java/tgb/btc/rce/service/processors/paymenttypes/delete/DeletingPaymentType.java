package tgb.btc.rce.service.processors.paymenttypes.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETING_PAYMENT_TYPE)
public class DeletingPaymentType extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private ShowPaymentTypesForDelete showPaymentTypesForDelete;

    @Autowired
    public void setShowPaymentTypesForDelete(ShowPaymentTypesForDelete showPaymentTypesForDelete) {
        this.showPaymentTypesForDelete = showPaymentTypesForDelete;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public DeletingPaymentType(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long pid = Long.valueOf(values[1]);
        PaymentType paymentType = paymentTypeRepository.getByPid(pid);
        DealType dealType = paymentType.getDealType();
        String message = "Тип оплаты на " + paymentType.getDealType().getDisplayName() + " \"" + paymentType.getName() + "\" удален.";
        paymentTypeRepository.deleteById(pid);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        responseSender.sendMessage(UpdateUtil.getChatId(update), message);
        showPaymentTypesForDelete.sendPaymentTypes(chatId, dealType, paymentType.getFiatCurrency());
    }
}
