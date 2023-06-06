package tgb.btc.lib.service.processors.paymenttypes.requisite.create;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.lib.annotation.CommandProcessor;
import tgb.btc.lib.bean.PaymentType;
import tgb.btc.lib.constants.BotStringConstants;
import tgb.btc.lib.enums.BotKeyboard;
import tgb.btc.lib.enums.Command;
import tgb.btc.lib.enums.DealType;
import tgb.btc.lib.repository.PaymentTypeRepository;
import tgb.btc.lib.service.Processor;
import tgb.btc.lib.util.FiatCurrencyUtil;
import tgb.btc.lib.util.KeyboardUtil;
import tgb.btc.lib.util.UpdateUtil;
import tgb.btc.lib.vo.InlineButton;

import java.util.List;
import java.util.stream.Collectors;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE_REQUISITE)
public class FiatCurrencyCreateRequisite extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    public void setPaymentTypeRepository(PaymentTypeRepository paymentTypeRepository) {
        this.paymentTypeRepository = paymentTypeRepository;
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        if (FiatCurrencyUtil.isFew()) {
            responseSender.sendMessage(chatId, BotStringConstants.FIAT_CURRENCY_CHOOSE, BotKeyboard.FIAT_CURRENCIES);
        } else {
            List<PaymentType> paymentTypes = paymentTypeRepository.getByDealTypeAndFiatCurrency(DealType.BUY, FiatCurrencyUtil.getFirst());
            if (CollectionUtils.isEmpty(paymentTypes)) {
                responseSender.sendMessage(chatId, "Список тип оплат на " + DealType.BUY.getAccusative() + "-" + FiatCurrencyUtil.getFirst().getCode() + " пуст.");
                processToAdminMainPanel(chatId);
                return;
            }

            List<InlineButton> buttons = paymentTypes.stream()
                    .map(paymentType -> InlineButton.builder()
                            .text(paymentType.getName())
                            .data(Command.NEW_PAYMENT_TYPE_REQUISITE.getText()
                                    + BotStringConstants.CALLBACK_DATA_SPLITTER + paymentType.getPid())
                            .build())
                    .collect(Collectors.toList());
            responseSender.sendMessage(chatId, "Выберите тип оплаты для добавления реквизита.",
                    KeyboardUtil.buildInline(buttons));
            responseSender.sendMessage(chatId, "Для возвращения в меню нажмите \"Отмена\".", BotKeyboard.REPLY_CANCEL);
            userService.nextStep(chatId, Command.NEW_PAYMENT_TYPE_REQUISITE);
        }
        userService.nextStep(chatId, Command.NEW_PAYMENT_TYPE_REQUISITE);
    }
}
