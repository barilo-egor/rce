package tgb.btc.rce.service.handler.impl.callback.settings;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.constants.enums.DeliveryKind;
import tgb.btc.library.interfaces.IModule;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.IKeyboardService;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.keyboard.IKeyboardBuildService;
import tgb.btc.rce.service.util.ICallbackDataService;

import java.util.List;

@Service
public class TurningProcessDeliveryHandler implements ICallbackQueryHandler {

    private final ICallbackDataService callbackDataService;

    private final IModule<DeliveryKind> deliveryKindModule;

    private final IKeyboardService keyboardService;

    private final IKeyboardBuildService keyboardBuildService;

    private final IResponseSender responseSender;

    public TurningProcessDeliveryHandler(ICallbackDataService callbackDataService,
                                         IModule<DeliveryKind> deliveryKindModule, IKeyboardService keyboardService,
                                         IKeyboardBuildService keyboardBuildService, IResponseSender responseSender) {
        this.callbackDataService = callbackDataService;
        this.deliveryKindModule = deliveryKindModule;
        this.keyboardService = keyboardService;
        this.keyboardBuildService = keyboardBuildService;
        this.responseSender = responseSender;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        String deliveryKind = callbackDataService.getArgument(callbackQuery.getData(), 1);
        deliveryKindModule.set(DeliveryKind.valueOf(deliveryKind));
        responseSender.sendEditedMessageText(chatId, callbackQuery.getMessage().getMessageId(),
                "Вкл/выкл способов доставки",
                keyboardBuildService.buildInline(List.of(keyboardService.getDeliveryTypeButton())));
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.TURN_PROCESS_DELIVERY;
    }
}
