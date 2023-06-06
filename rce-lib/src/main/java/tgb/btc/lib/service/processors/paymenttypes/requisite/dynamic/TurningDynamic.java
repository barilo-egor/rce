package tgb.btc.lib.service.processors.paymenttypes.requisite.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.PaymentRequisite;
import tgb.btc.lib.bean.PaymentType;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.repository.PaymentRequisiteRepository;
import tgb.btc.lib.repository.PaymentTypeRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.UpdateUtil;

import java.util.List;

@CommandProcessor(command = Command.TURNING_DYNAMIC_REQUISITES)
public class TurningDynamic extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private PaymentRequisiteRepository paymentRequisiteRepository;

    private TurnDynamicRequisites turnDynamicRequisites;

    @Autowired
    public void setTurnDynamicRequisites(TurnDynamicRequisites turnDynamicRequisites) {
        this.turnDynamicRequisites = turnDynamicRequisites;
    }

    @Autowired
    public void setPaymentRequisiteRepository(PaymentRequisiteRepository paymentRequisiteRepository) {
        this.paymentRequisiteRepository = paymentRequisiteRepository;
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
        boolean value = Boolean.parseBoolean(values[2]);
        Long pid = Long.parseLong(values[1]);
        PaymentType paymentType = paymentTypeRepository.getByPid(pid);
        if (!value) {
            paymentTypeRepository.updateIsDynamicOnByPid(false, pid);
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            responseSender.sendMessage(chatId, "Динамические реквизиты для " + paymentType.getName() + " выключены.");
            turnDynamicRequisites.sendPaymentTypes(chatId, paymentType.getDealType(), paymentType.getFiatCurrency());
        } else {
            List<PaymentRequisite> paymentRequisites = paymentRequisiteRepository.getByPaymentTypePid(pid);
            if (paymentRequisites.size() <= 1) {
                responseSender.sendMessage(chatId, "Недостаточно реквизитов для включения. Количество реквизитов: "
                        + paymentRequisites.size() + ".");
                return;
            }
            paymentTypeRepository.updateIsDynamicOnByPid(true, pid);
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            responseSender.sendMessage(chatId, "Динамические реквизиты для " + paymentType.getName() + " включены.");
            turnDynamicRequisites.sendPaymentTypes(chatId, paymentType.getDealType(), paymentType.getFiatCurrency());
        }
    }

}
