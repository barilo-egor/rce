package tgb.btc.rce.service.processors.admin.settings.paymenttypes.turning;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

@CommandProcessor(command = Command.TURNING_PAYMENT_TYPES)
public class TurningPaymentType extends Processor {

    private IPaymentTypeService paymentTypeService;

    private ShowPaymentTypesForTurn showPaymentTypesForTurn;

    private IPaymentRequisiteService paymentRequisiteService;

    @Autowired
    public void setPaymentRequisiteService(IPaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Autowired
    public void setShowPaymentTypesForTurn(ShowPaymentTypesForTurn showPaymentTypesForTurn) {
        this.showPaymentTypesForTurn = showPaymentTypesForTurn;
    }
    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasCallbackQuery()) {
            return;
        }
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long paymentTypePid = Long.parseLong(values[1]);
        paymentTypeService.updateIsOnByPid(Boolean.valueOf(values[2]), paymentTypePid);
        paymentRequisiteService.removeOrder(paymentTypePid);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        showPaymentTypesForTurn.sendPaymentTypes(chatId, paymentType.getDealType(), paymentType.getFiatCurrency());
    }

}
