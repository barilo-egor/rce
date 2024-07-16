package tgb.btc.rce.service.processors.admin.settings.paymenttypes.requisite.delete;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.DELETE_PAYMENT_TYPE_REQUISITE, step = 2)
public class ShowRequisitesForDelete extends Processor {

    private IPaymentRequisiteService paymentRequisiteService;

    @Autowired
    public void setPaymentRequisiteService(IPaymentRequisiteService paymentRequisiteService) {
        this.paymentRequisiteService = paymentRequisiteService;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (!update.hasCallbackQuery()) {
            responseSender.sendMessage(chatId, "Выберите тип оплаты.");
            return;
        }
        String[] values = update.getCallbackQuery().getData().split(BotStringConstants.CALLBACK_DATA_SPLITTER);
        sendRequisites(chatId, Long.parseLong(values[1]));
        processToAdminMainPanel(chatId);
    }


    public void sendRequisites(Long chatId, Long paymentTypePid) {
        List<PaymentRequisite> paymentRequisites = paymentRequisiteService.getByPaymentType_Pid(paymentTypePid);
        if (CollectionUtils.isEmpty(paymentRequisites)) {
            responseSender.sendMessage(chatId, "Реквизиты в этом типе оплаты отсутствуют.");
            processToAdminMainPanel(chatId);
            return;
        }
        StringBuilder message = new StringBuilder();

        message.append("Выберите номер реквизита для удаления. \nСуществующие реквизиты:\n");
        int counter = 1;
        List<InlineButton> buttons = new ArrayList<>();
        for (PaymentRequisite paymentRequisite : paymentRequisites) {
            message.append(counter).append(". ").append(paymentRequisite.getRequisite())
                    .append("\n").append("---------------\n");
            buttons.add(InlineButton.builder()
                                .text(String.valueOf(counter))
                                .data(Command.DELETING_PAYMENT_TYPE_REQUISITE.getText()
                                              + BotStringConstants.CALLBACK_DATA_SPLITTER + paymentRequisite.getPid())
                                .build());
            counter++;
        }
        responseSender.sendMessage(chatId, message.toString(), keyboardBuildService.buildInline(buttons));
    }
}
