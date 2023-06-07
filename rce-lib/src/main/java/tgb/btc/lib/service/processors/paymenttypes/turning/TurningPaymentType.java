package tgb.btc.lib.service.processors.paymenttypes.turning;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.PaymentType;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.PaymentTypeRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

@CommandProcessor(command = Command.TURNING_PAYMENT_TYPES)
public class TurningPaymentType extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private ShowPaymentTypesForTurn showPaymentTypesForTurn;

    @Autowired
    public void setShowPaymentTypesForTurn(ShowPaymentTypesForTurn showPaymentTypesForTurn) {
        this.showPaymentTypesForTurn = showPaymentTypesForTurn;
    }

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasCallbackQuery()) {
            return;
        }
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        Long paymentTypePid = Long.parseLong(values[1]);
        paymentTypeRepository.updateIsOnByPid(Boolean.valueOf(values[2]), paymentTypePid);
        responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
        PaymentType paymentType = paymentTypeRepository.getByPid(paymentTypePid);
        showPaymentTypesForTurn.sendPaymentTypes(chatId, paymentType.getDealType(), paymentType.getFiatCurrency());
    }

}
