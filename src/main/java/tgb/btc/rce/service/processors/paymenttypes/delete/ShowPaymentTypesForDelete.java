package tgb.btc.rce.service.processors.paymenttypes.delete;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.DELETE_PAYMENT_TYPE, step = 1)
public class ShowPaymentTypesForDelete extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Autowired
    public ShowPaymentTypesForDelete(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
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

        List<InlineButton> buttons = paymentTypes.stream()
                .map(paymentType -> InlineButton.builder()
                        .text(paymentType.getName())
                        .data(Command.DELETING_PAYMENT_TYPE.getText()
                                      + BotStringConstants.CALLBACK_DATA_SPLITTER + paymentType.getPid())
                        .build())
                .collect(Collectors.toList());
        buttons.add(InlineButton.builder()
                            .text("Отмена")
                            .data(Command.INLINE_DELETE.getText())
                            .build());
        responseSender.sendMessage(chatId, "Выберите тип оплаты для удаления.", KeyboardUtil.buildInline(buttons));
    }

}