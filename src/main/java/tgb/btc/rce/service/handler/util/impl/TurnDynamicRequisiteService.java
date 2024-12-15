package tgb.btc.rce.service.handler.util.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.ITurnDynamicRequisiteService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class TurnDynamicRequisiteService implements ITurnDynamicRequisiteService {

    private final IResponseSender responseSender;

    private final IPaymentTypeService paymentTypeService;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    public TurnDynamicRequisiteService(IResponseSender responseSender, IPaymentTypeService paymentTypeService,
                                       ICallbackDataService callbackDataService,
                                       IKeyboardBuildService keyboardBuildService) {
        this.responseSender = responseSender;
        this.paymentTypeService = paymentTypeService;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void sendPaymentTypes(Long chatId, Integer messageId, FiatCurrency fiatCurrency) {
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndFiatCurrency(DealType.BUY, fiatCurrency);
        if (CollectionUtils.isEmpty(paymentTypes)) {
            responseSender.sendMessage(chatId, "Список тип оплат покупки <b>" + fiatCurrency.getDisplayName() + "</b> пуст.");
            return;
        }
        List<InlineButton> buttons = new ArrayList<>();
        for (PaymentType paymentType : paymentTypes) {
            boolean isDynamicOn = BooleanUtils.isTrue(paymentType.getDynamicOn());
            String text = paymentType.getName() + " - " +
                    (isDynamicOn ? "выключить" : "включить");
            String strIsOn = isDynamicOn ? Boolean.FALSE.toString() : Boolean.TRUE.toString();
            buttons.add(InlineButton.builder()
                    .text(text)
                    .data(callbackDataService.buildData(CallbackQueryData.TURNING_DYNAMIC_REQUISITES, paymentType.getPid(), strIsOn))
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text("❌ Закрыть")
                .data(CallbackQueryData.INLINE_DELETE.name())
                .build());
        responseSender.sendEditedMessageText(chatId, messageId,
                "Выберите тип оплаты(<b>" + fiatCurrency.getDisplayName() + "</b>) для включения/выключения динамических реквизитов.",
                keyboardBuildService.buildInline(buttons));
    }
}
