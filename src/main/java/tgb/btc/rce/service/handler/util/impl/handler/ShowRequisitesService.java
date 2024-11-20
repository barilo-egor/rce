package tgb.btc.rce.service.handler.util.impl.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.PaymentRequisite;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentRequisiteService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IShowRequisitesService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShowRequisitesService implements IShowRequisitesService {

    private final IPaymentRequisiteService paymentRequisiteService;

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    private final IAdminPanelService adminPanelService;

    private final ICallbackDataService callbackDataService;

    public ShowRequisitesService(IPaymentRequisiteService paymentRequisiteService, IResponseSender responseSender,
                                 IKeyboardBuildService keyboardBuildService, IAdminPanelService adminPanelService,
                                 ICallbackDataService callbackDataService) {
        this.paymentRequisiteService = paymentRequisiteService;
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
        this.adminPanelService = adminPanelService;
        this.callbackDataService = callbackDataService;
    }

    @Override
    public void showForDelete(Long chatId, Long paymentTypePid) {
        List<PaymentRequisite> paymentRequisites = paymentRequisiteService.getByPaymentType_Pid(paymentTypePid);
        if (CollectionUtils.isEmpty(paymentRequisites)) {
            responseSender.sendMessage(chatId, "Реквизиты в этом типе оплаты отсутствуют.");
            adminPanelService.send(chatId);
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
                    .data(callbackDataService.buildData(CallbackQueryData.DELETING_PAYMENT_TYPE_REQUISITE, paymentRequisite.getPid()))
                    .build());
            counter++;
        }
        responseSender.sendMessage(chatId, message.toString(), keyboardBuildService.buildInline(buttons));
    }
}
