package tgb.btc.rce.service.handler.impl.callback.settings.payment;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.web.merchant.paypoints.PayPointsMethod;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class PayPointsBindingCallbackHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final IResponseSender responseSender;

    private final IKeyboardBuildService keyboardBuildService;

    public PayPointsBindingCallbackHandler(ICallbackDataService callbackDataService, IPaymentTypeService paymentTypeService,
                                           IResponseSender responseSender, IKeyboardBuildService keyboardBuildService) {
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.responseSender = responseSender;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long paymentTypePid = callbackDataService.getLongArgument(callbackQuery.getData(), 1);
        PaymentType paymentType = paymentTypeService.getByPid(paymentTypePid);
        responseSender.deleteMessage(chatId, callbackQuery.getMessage().getMessageId());
        List<InlineButton> buttons = new ArrayList<>(Arrays.stream(PayPointsMethod.values())
                .map(method -> InlineButton.builder()
                        .text(method.getDisplayName())
                        .data(callbackDataService.buildData(CallbackQueryData.PAY_POINTS_METHOD, paymentTypePid, method.name()))
                        .build())
                .toList());
        if (Objects.isNull(paymentType.getPayPointsMethod())) {
            responseSender.sendMessage(chatId, "Привязка типа оплаты <b>\"" + paymentType.getName()
                            + "\"</b> к методу оплаты PayPoints отсутствует. Выберите новый метод.",
                    keyboardBuildService.buildInline(buttons, 1));
        } else {
            buttons.add(InlineButton.builder()
                    .text("❌ Удалить привязку")
                    .data(callbackDataService.buildData(CallbackQueryData.PAY_POINTS_METHOD, paymentTypePid))
                    .build());
            responseSender.sendMessage(chatId, "Тип оплаты <b>\"" + paymentType.getName()
                            + "\"</b> привязан к PayPoints методу оплаты <b>\""
                            + paymentType.getPayPointsMethod().getDisplayName() + "\"</b>.",
                    keyboardBuildService.buildInline(buttons, 1));
        }
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.PAY_POINTS_BINDING;
    }
}
