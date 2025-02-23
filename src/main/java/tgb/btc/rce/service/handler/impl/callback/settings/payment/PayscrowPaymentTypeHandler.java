package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class PayscrowPaymentTypeHandler implements ICallbackQueryHandler {

    private final Map<String, String> PAYMENT_METHODS_IDS = Map.of(
            "Альфа-Банк", "4f591bcc-29f8-4598-9828-5b109a25b509",
            "Сбербанк", "af3daf65-b6b6-450b-b28d-54b97436ef4a",
            "Тинькофф", "cd797fcb-5b5a-47a3-8b54-35f5f7000344",
            "Любой банк РФ", "b11d98ad-1e09-4e63-8c4a-f1d8a4b9ce3f",

            "СБП Тинькофф", "33c6fe18-641d-41f5-a3fc-db975c63fe6f",
            "СБП Альфа-Банк", "8880d5b5-2c82-4cef-8f94-b7d5d2cbefe1",
            "СБП Сбербанк", "a6636989-0bd9-4cf1-8ec3-83c07b08f25f",
            "СБП", "894387d7-b8b6-4dab-82ee-dd1106f7369e"
    );

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final IKeyboardBuildService keyboardBuildService;

    public PayscrowPaymentTypeHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                      IPaymentTypeService paymentTypeService, IKeyboardBuildService keyboardBuildService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        List<InlineButton> buttons = new java.util.ArrayList<>(PAYMENT_METHODS_IDS.entrySet().stream()
                .map(entry ->
                        InlineButton.builder()
                                .text(entry.getKey())
                                .data(callbackDataService.buildData(CallbackQueryData.PAYSCROW_METHOD_ID, paymentTypePid, entry.getValue()))
                                .build())
                .toList());
        if (Objects.isNull(paymentType.getPayscrowPaymentMethodId())) {
            responseSender.sendMessage(chatId, "Привязка типа оплаты <b>\"" + paymentType.getName()
                            + "\"</b> к методу оплаты Payscrow отсутствует. Выберите новый метод.",
                    keyboardBuildService.buildInline(buttons, 2));
        } else {
            buttons.add(InlineButton.builder()
                    .text("❌ Удалить привязку")
                    .data(callbackDataService.buildData(CallbackQueryData.PAYSCROW_METHOD_ID, paymentTypePid))
                    .build());
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> привязан к Payscrow методу оплаты <b>\"" + getPaymentMethodName(paymentType.getPayscrowPaymentMethodId()) + "\"</b>.", buttons);
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PAYSCROW_PAYMENT_TYPE;
    }


    public String getPaymentMethodName(String methodId) {
        for (Map.Entry<String, String> entry : PAYMENT_METHODS_IDS.entrySet()) {
            if (methodId.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
