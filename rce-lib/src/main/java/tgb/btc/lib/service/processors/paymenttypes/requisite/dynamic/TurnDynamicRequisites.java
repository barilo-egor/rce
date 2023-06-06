package tgb.btc.lib.service.processors.paymenttypes.requisite.dynamic;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.PaymentType;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.DealType;
import tgb.btc.lib.enums.FiatCurrency;
import tgb.btc.lib.repository.PaymentTypeRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.FiatCurrencyUtil;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.TURN_DYNAMIC_REQUISITES, step = 1)
public class TurnDynamicRequisites extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        FiatCurrency fiatCurrency = FiatCurrencyUtil.isFew()
                ? FiatCurrency.getByCode(UpdateUtil.getMessageText(update))
                : FiatCurrencyUtil.getFirst();
        sendPaymentTypes(chatId, DealType.BUY, fiatCurrency);
        processToAdminMainPanel(chatId);
    }

    public void sendPaymentTypes(Long chatId, DealType dealType, FiatCurrency fiatCurrency) {
        List<PaymentType> paymentTypes = paymentTypeRepository.getByDealTypeAndFiatCurrency(dealType, fiatCurrency);
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
            String data = Command.TURNING_DYNAMIC_REQUISITES.getText()
                    + BotStringConstants.CALLBACK_DATA_SPLITTER + paymentType.getPid()
                    + BotStringConstants.CALLBACK_DATA_SPLITTER + (isDynamicOn ? Boolean.FALSE.toString() : Boolean.TRUE.toString());
            buttons.add(InlineButton.builder()
                    .text(text)
                    .data(data)
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text("❌ Закрыть")
                .data(Command.INLINE_DELETE.getText())
                .build());
        responseSender.sendMessage(chatId, "Выберите тип оплаты для включения/выключения динамических реквизитов.",
                KeyboardUtil.buildInline(buttons));
    }
}
