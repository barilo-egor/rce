package tgb.btc.rce.service.processors.paymenttypes.delete;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.DELETING_PAYMENT_TYPE)
public class DeletingPaymentType extends Processor {


    private IPaymentTypeService paymentTypeService;

    private IPaymentRequisiteService paymentRequisiteService;

    private ShowPaymentTypesForDelete showPaymentTypesForDelete;

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Autowired
    public void setPaymentRequisiteService(IPaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Autowired
    public void setShowPaymentTypesForDelete(ShowPaymentTypesForDelete showPaymentTypesForDelete) {
        this.showPaymentTypesForDelete = showPaymentTypesForDelete;
    }

    @Override
    public void run(Update update) {
        if (!update.hasCallbackQuery()) return;
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long pid = Long.valueOf(values[1]);
        PaymentType paymentType = paymentTypeService.getByPid(pid);
        DealType dealType = paymentType.getDealType();
        paymentRequisiteService.deleteByPaymentTypePid(paymentType.getPid());
        paymentTypeService.deleteById(pid);
        Long chatId = UpdateUtil.getChatId(update);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        String message = "Тип оплаты на " + paymentType.getDealType().getAccusative() + " \"" + paymentType.getName() + "\" удален.";
        responseSender.sendMessage(UpdateUtil.getChatId(update), message);
        showPaymentTypesForDelete.sendPaymentTypes(chatId, dealType, paymentType.getFiatCurrency());
    }
}
