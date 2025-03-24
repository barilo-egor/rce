package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.Merchant;
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
        Merchant merchant = Merchant.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 3));
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        if (!isDelete) {
            switch (merchant) {
                case ALFA_TEAM_TJS -> paymentType.setAlfaTeamTJSPaymentOption(PaymentOption.CROSS_BORDER);
                case ALFA_TEAM_VTB -> paymentType.setAlfaTeamVTBPaymentOption(PaymentOption.SBP);
                case ALFA_TEAM_ALFA -> paymentType.setAlfaTeamAlfaPaymentOption(PaymentOption.SBP);
                case ALFA_TEAM_SBER -> paymentType.setAlfaTeamSberPaymentOption(PaymentOption.TO_CARD);
            }
            paymentTypeService.save(paymentType);
            responseSender.sendMessage(chatId, merchant.getDisplayName() + " привязан к типу оплаты <b>\"" + paymentType.getName() + "\"</b>.");
        } else {
            switch (merchant) {
                case ALFA_TEAM_TJS -> paymentType.setAlfaTeamTJSPaymentOption(null);
                case ALFA_TEAM_VTB -> paymentType.setAlfaTeamVTBPaymentOption(null);
                case ALFA_TEAM_ALFA -> paymentType.setAlfaTeamAlfaPaymentOption(null);
                case ALFA_TEAM_SBER -> paymentType.setAlfaTeamSberPaymentOption(null);
            }
            paymentTypeService.save(paymentType);
            responseSender.sendMessage(chatId, merchant.getDisplayName() + " отвязан от типа оплаты <b>\"" + paymentType.getName() + "\"</b>");
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.ALFA_TEAM_PAYMENT_TYPE_BINDING;
    }
}
