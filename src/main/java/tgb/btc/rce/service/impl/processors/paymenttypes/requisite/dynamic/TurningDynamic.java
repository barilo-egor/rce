package tgb.btc.rce.service.impl.processors.paymenttypes.requisite.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;

import java.util.List;

@CommandProcessor(command = Command.TURNING_DYNAMIC_REQUISITES)
public class TurningDynamic extends Processor {

    private IPaymentTypeService paymentTypeService;

    private IPaymentRequisiteService paymentRequisiteService;

    private TurnDynamicRequisites turnDynamicRequisites;

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Autowired
    public void setPaymentRequisiteService(IPaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Autowired
    public void setTurnDynamicRequisites(TurnDynamicRequisites turnDynamicRequisites) {
        this.turnDynamicRequisites = turnDynamicRequisites;
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
        PaymentType paymentType = paymentTypeService.getByPid(pid);
        if (!value) {
            paymentTypeService.updateIsDynamicOnByPid(false, pid);
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            responseSender.sendMessage(chatId, "Динамические реквизиты для " + paymentType.getName() + " выключены.");
            turnDynamicRequisites.sendPaymentTypes(chatId, paymentType.getDealType(), paymentType.getFiatCurrency());
        } else {
            List<PaymentRequisite> paymentRequisites = paymentRequisiteService.getByPaymentType_Pid(pid);
            if (paymentRequisites.size() <= 1) {
                responseSender.sendMessage(chatId, "Недостаточно реквизитов для включения. Количество реквизитов: "
                        + paymentRequisites.size() + ".");
                return;
            }
            paymentTypeService.updateIsDynamicOnByPid(true, pid);
            responseSender.deleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId());
            responseSender.sendMessage(chatId, "Динамические реквизиты для " + paymentType.getName() + " включены.");
            turnDynamicRequisites.sendPaymentTypes(chatId, paymentType.getDealType(), paymentType.getFiatCurrency());
        }
    }

}
