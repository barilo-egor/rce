package tgb.btc.rce.service.processors.admin.settings.paymenttypes.requisite.dynamic;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.interfaces.service.bean.bot.IPaymentTypeService;
import tgb.btc.library.interfaces.util.IFiatCurrencyService;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.update.CallbackQueryData;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.util.ICallbackDataService;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.TURN_DYNAMIC_REQUISITES, step = 1)
public class TurnDynamicRequisites extends Processor {

    private IPaymentTypeService paymentTypeService;

    private IFiatCurrencyService fiatCurrencyService;

    private ICallbackDataService callbackDataService;

    @Autowired
    public void setCallbackDataService(ICallbackDataService callbackDataService) {
        this.callbackDataService = callbackDataService;
    }

    @Autowired
    public void setFiatCurrencyService(IFiatCurrencyService fiatCurrencyService) {
        this.fiatCurrencyService = fiatCurrencyService;
    }

    @Autowired
    public void setPaymentTypeService(IPaymentTypeService paymentTypeService) {
        this.paymentTypeService = paymentTypeService;
    }

    @Override
    public void run(Update update) {
        Long chatId = updateService.getChatId(update);
        FiatCurrency fiatCurrency = fiatCurrencyService.isFew()
                ? FiatCurrency.getByCode(updateService.getMessageText(update))
                : fiatCurrencyService.getFirst();
        sendPaymentTypes(chatId, DealType.BUY, fiatCurrency);
        processToAdminMainPanel(chatId);
    }

    public void sendPaymentTypes(Long chatId, DealType dealType, FiatCurrency fiatCurrency) {
        List<PaymentType> paymentTypes = paymentTypeService.getByDealTypeAndFiatCurrency(dealType, fiatCurrency);
        if (CollectionUtils.isEmpty(paymentTypes)) {
            responseSender.sendMessage(chatId, "Список тип оплат на " + dealType.getAccusative() + " пуст."); // TODO добавить фиат
            processToAdminMainPanel(chatId);
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
        responseSender.sendMessage(chatId, "Выберите тип оплаты для включения/выключения динамических реквизитов.",
                keyboardBuildService.buildInline(buttons));
    }
}
