package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.web.merchant.alfateam.PaymentOption;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.Objects;

@Service
public class AlfaTeamMethodIdHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final IResponseSender responseSender;

    public AlfaTeamMethodIdHandler(ICallbackDataService callbackDataService, IPaymentTypeService paymentTypeService, IResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData();
        Long paymentTypePid = callbackDataService.getLongArgument(data, 1);
        String methodName = callbackDataService.getArgument(data, 2);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        if (Objects.nonNull(methodName)) {
            PaymentOption paymentOption = PaymentOption.valueOf(methodName);
            paymentType.setAlfaTeamPaymentOption(paymentOption);
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> связан с AlfaTeam методом оплаты <b>\"" + paymentOption.getDescription()
                    + "\"</b>.");
        } else {
            paymentType.setAlfaTeamPaymentOption(null);
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> отвязан от AlfaTeam метода оплаты.");
        }
        paymentTypeService.save(paymentType);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ALFA_TEAM_METHOD_ID;
    }
}
