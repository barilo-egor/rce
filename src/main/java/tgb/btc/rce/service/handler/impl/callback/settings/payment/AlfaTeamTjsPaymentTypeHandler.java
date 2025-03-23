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

@Service
public class AlfaTeamTjsPaymentTypeHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final IResponseSender responseSender;

    public AlfaTeamTjsPaymentTypeHandler(ICallbackDataService callbackDataService, IPaymentTypeService paymentTypeService,
                                         IResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        Boolean isDelete = callbackDataService.getBoolArgument(callbackQuery.getData(), 2);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        if (!isDelete) {
            paymentType.setAlfaTeamTJSPaymentOption(PaymentOption.CROSS_BORDER);
            paymentTypeService.save(paymentType);
            responseSender.sendMessage(chatId, "AlfaTeam TJS привязан к типу оплаты <b>\"" + paymentType.getName() + "\"</b>.");
        } else {
            paymentType.setAlfaTeamTJSPaymentOption(null);
            paymentTypeService.save(paymentType);
            responseSender.sendMessage(chatId, "AlfaTeam TJS отвязан от типа оплаты <b>\"" + paymentType.getName() + "\"</b>");
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ALFA_TEAM_TJS_PAYMENT_TYPE;
    }
}
