package tgb.btc.rce.service.processors.paymenttypes.turning;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.TURN_PAYMENT_TYPES, step = 1)
public class ShowPaymentTypesForTurn extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public void run(Update update) {
        if (!hasMessageText(update, BotStringConstants.BUY_OR_SELL)) {
            return;
        }
        Long chatId = UpdateUtil.getChatId(update);
        String message = UpdateUtil.getMessageText(update);
        DealType dealType;
        if (BotStringConstants.BUY.equals(message)) {
            dealType = DealType.BUY;
        } else if (BotStringConstants.SELL.equals(message)) {
            dealType = DealType.SELL;
        } else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL);
            return;
        }
        sendPaymentTypes(chatId, dealType);
        processToAdminMainPanel(chatId);
    }

    public void sendPaymentTypes(Long chatId, DealType dealType) {
        List<PaymentType> paymentTypes = paymentTypeRepository.getByDealType(dealType);
        if (CollectionUtils.isEmpty(paymentTypes)) {
            responseSender.sendMessage(chatId, "Список тип оплат на " + dealType.getDisplayName() + " пуст.");
            processToAdminMainPanel(chatId);
            return;
        }
        List<InlineButton> buttons = new ArrayList<>();
        for (PaymentType paymentType : paymentTypes) {
            boolean isOn = BooleanUtils.isTrue(paymentType.getOn());
            String text = paymentType.getName() + " - " +
                    (isOn ? "выключить" : "включить");
            String data = Command.TURNING_PAYMENT_TYPES.getText()
                    + BotStringConstants.CALLBACK_DATA_SPLITTER + paymentType.getPid()
                    + BotStringConstants.CALLBACK_DATA_SPLITTER + (isOn ? Boolean.FALSE.toString() : Boolean.TRUE.toString());
            buttons.add(InlineButton.builder()
                    .text(text)
                    .data(data)
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text("❌ Закрыть")
                .data(Command.INLINE_DELETE.getText())
                .build());
        responseSender.sendMessage(chatId, "Выберите тип оплаты для включения/выключения.",
                KeyboardUtil.buildInline(buttons));
    }

}
