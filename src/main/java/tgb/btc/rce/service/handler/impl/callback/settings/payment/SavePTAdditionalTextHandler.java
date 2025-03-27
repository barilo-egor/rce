package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.util.ICallbackDataService;

@Service
public class SavePTAdditionalTextHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    public SavePTAdditionalTextHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                       IPaymentTypeService paymentTypeService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        String additionalText = callbackQuery.getMessage().getText();
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        paymentType.setRequisiteAdditionalText(additionalText);
        paymentTypeService.save(paymentType);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        responseSender.sendMessage(chatId, "Дополнительный текст реквизита для типа оплаты <b>\"" + paymentType.getName() + "\"</b> сохранен.");
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.SAVE_ADDITIONAL_PT_TEXT;
    }
}
