package tgb.btc.rce.service.handler.impl.callback.settings.payment.delete;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.sender.IResponseSender;
import tgb.btc.rce.service.handler.ICallbackQueryHandler;
import tgb.btc.rce.service.handler.util.IAdminPanelService;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealTypeDeletePaymentTypeHandler implements ICallbackQueryHandler {

    private final IResponseSender responseSender;

    private final ICallbackDataService callbackDataService;

    private final IPaymentTypeService paymentTypeService;

    private final IAdminPanelService adminPanelService;

    public DealTypeDeletePaymentTypeHandler(IResponseSender responseSender,
                                            ICallbackDataService callbackDataService,
                                            IPaymentTypeService paymentTypeService,
                                            IAdminPanelService adminPanelService) {
        this.responseSender = responseSender;
        this.callbackDataService = callbackDataService;
        this.paymentTypeService = paymentTypeService;
        this.adminPanelService = adminPanelService;
    }

    @Override
    public void handle(CallbackQuery callbackQuery) {
        DealType dealType = DealType.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 1));
        FiatCurrency fiatCurrency = FiatCurrency.valueOf(callbackDataService.getArgument(callbackQuery.getData(), 2));
        Long chatId = callbackQuery.getFrom().getId();
        sendPaymentTypes(chatId, callbackQuery.getMessage().getMessageId(), dealType, fiatCurrency);
        adminPanelService.send(chatId);
    }

    public void sendPaymentTypes(Long chatId, Integer messageId, DealType dealType, FiatCurrency fiatCurrency) {
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndFiatCurrency(dealType, fiatCurrency);
        if (CollectionUtils.isEmpty(paymentTypes)) {
            responseSender.deleteMessage(chatId, messageId);
            responseSender.sendMessage(chatId, "Список тип оплат на " + dealType.getAccusative() + " пуст.");
            adminPanelService.send(chatId);
            return;
        }

        List<InlineButton> buttons = paymentTypes.stream()
                .map(paymentType -> InlineButton.builder()
                        .text(paymentType.getName())
                        .data(callbackDataService.buildData(CallbackQueryData.DELETING_PAYMENT_TYPE, paymentType.getPid()))
                        .build())
                .collect(Collectors.toList());
        buttons.add(InlineButton.builder()
                .text("Отмена")
                .data(CallbackQueryData.INLINE_DELETE.name())
                .build());
        responseSender.sendEditedMessageText(chatId, messageId,
                "Выберите тип оплаты(<b>" + dealType.getNominativeFirstLetterToUpper()
                + "</b>, <b>" + fiatCurrency.getDisplayName() + "</b>) для удаления.", buttons);
    }

    @Override
    public CallbackQueryData getCallbackQueryData() {
        return CallbackQueryData.DEAL_TYPE_DELETE_PAYMENT_TYPE;
    }
}
