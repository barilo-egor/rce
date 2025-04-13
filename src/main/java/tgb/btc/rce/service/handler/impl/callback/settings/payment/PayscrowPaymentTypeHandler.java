package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.service.web.merchant.payscrow.PayscrowMerchantService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.Objects;

@Service
public class PayscrowPaymentTypeHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final IKeyboardBuildService keyboardBuildService;

    private final PayscrowMerchantService payscrowMerchantService;

    public PayscrowPaymentTypeHandler(IResponseSender responseSender, ICallbackDataService callbackDataService,
                                      IPaymentTypeService paymentTypeService, IKeyboardBuildService keyboardBuildService,
                                      PayscrowMerchantService payscrowMerchantService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.keyboardBuildService = keyboardBuildService;
        this.payscrowMerchantService = payscrowMerchantService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        List<InlineButton> buttons = new java.util.ArrayList<>(payscrowMerchantService.getPaymentMethodsIds().entrySet().stream()
                .map(entry ->
                        InlineButton.builder()
                                .text(entry.getKey())
                                .data(callbackDataService.buildData(CallbackQueryData.PAYSCROW_METHOD, paymentTypePid, entry.getValue()))
                                .build())
                .toList());
        if (Objects.isNull(paymentType.getPayscrowPaymentMethodId())) {
            responseSender.sendMessage(chatId, "Привязка типа оплаты <b>\"" + paymentType.getName()
                            + "\"</b> к методу оплаты Payscrow отсутствует. Выберите новый метод.",
                    keyboardBuildService.buildInline(buttons, 2));
        } else {
            buttons.add(InlineButton.builder()
                    .text("❌ Удалить привязку")
                    .data(callbackDataService.buildData(CallbackQueryData.PAYSCROW_METHOD, paymentTypePid))
                    .build());
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                    + "\"</b> привязан к Payscrow методу оплаты <b>\""
                    + payscrowMerchantService.getPaymentMethodName(paymentType.getPayscrowPaymentMethodId()) + "\"</b>.",
                    keyboardBuildService.buildInline(buttons, 2));
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PAYSCROW_BINDING;
    }
}
