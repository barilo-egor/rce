package tgb.btc.rce.service.processors.paymenttypes.requisite.create;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.repository.bot.PaymentTypeRepository;
import tgb.btc.library.util.FiatCurrencyUtil;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.BotKeyboard;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

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
            userRepository.nextStep(chatId, Command.NEW_PAYMENT_TYPE_REQUISITE.name());
        }
        userRepository.nextStep(chatId, Command.NEW_PAYMENT_TYPE_REQUISITE.name());
    }
}
