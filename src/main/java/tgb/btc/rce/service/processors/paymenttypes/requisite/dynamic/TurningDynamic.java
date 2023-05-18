package tgb.btc.rce.service.processors.paymenttypes.requisite.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentRequisite;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.repository.PaymentRequisiteRepository;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.UpdateUtil;

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

    @Autowired
    public TurningDynamic(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
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
            turnDynamicRequisites.sendPaymentTypes(chatId, paymentType.getDealType());
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
            turnDynamicRequisites.sendPaymentTypes(chatId, paymentType.getDealType());
        }
    }

}
