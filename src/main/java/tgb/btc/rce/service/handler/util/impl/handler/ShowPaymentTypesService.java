package tgb.btc.rce.service.handler.util.impl.handler;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.handler.util.IShowPaymentTypesService;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShowPaymentTypesService implements IShowPaymentTypesService {

    private final IPaymentTypeService paymentTypeService;

    private final IResponseSender responseSender;

    private final IAdminPanelService adminPanelService;

    private final ICallbackDataService callbackDataService;

    private final IKeyboardBuildService keyboardBuildService;

    public ShowPaymentTypesService(IPaymentTypeService paymentTypeService, IResponseSender responseSender,
                                   IAdminPanelService adminPanelService, ICallbackDataService callbackDataService,
                                   IKeyboardBuildService keyboardBuildService) {
        this.paymentTypeService = paymentTypeService;
        this.responseSender = responseSender;
        this.adminPanelService = adminPanelService;
        this.callbackDataService = callbackDataService;
        this.keyboardBuildService = keyboardBuildService;
    }

    @Override
    public void sendForTurn(Long chatId, DealType dealType, FiatCurrency fiatCurrency, Integer messageId) {
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndFiatCurrency(dealType, fiatCurrency);
        if (CollectionUtils.isEmpty(paymentTypes)) {
            responseSender.sendMessage(chatId, "Список тип оплат на " + dealType.getAccusative() + "(" + fiatCurrency.getDisplayName() + ") пуст.");
            adminPanelService.send(chatId);
            return;
        }
        List<InlineButton> buttons = new ArrayList<>();
        for (PaymentType paymentType : paymentTypes) {
            boolean isOn = BooleanUtils.isTrue(paymentType.getOn());
            String text = paymentType.getName() + " - " +
                    (isOn ? "выключить" : "включить");
            String strIsOn = isOn ? Boolean.FALSE.toString() : Boolean.TRUE.toString();
            buttons.add(InlineButton.builder()
                    .text(text)
                    .data(callbackDataService.buildData(CallbackQueryData.TURNING_PAYMENT_TYPES, paymentType.getPid(), strIsOn))
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text("Закрыть")
                .data(CallbackQueryData.INLINE_DELETE.name())
                .build());
        responseSender.sendEditedMessageText(chatId, messageId, "Выберите тип оплаты для включения/выключения.",
                keyboardBuildService.buildInline(buttons));
    }
}
