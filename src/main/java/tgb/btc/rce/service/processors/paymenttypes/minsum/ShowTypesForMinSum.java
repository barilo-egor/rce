package tgb.btc.rce.service.processors.paymenttypes.minsum;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.library.bean.bot.PaymentType;
import tgb.btc.library.constants.enums.bot.DealType;
import tgb.btc.library.constants.enums.bot.FiatCurrency;
import tgb.btc.library.repository.bot.PaymentTypeRepository;
import tgb.btc.library.repository.bot.UserDataRepository;
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

@CommandProcessor(command = Command.CHANGE_MIN_SUM, step = 2)
public class ShowTypesForMinSum extends Processor {

    private PaymentTypeRepository paymentTypeRepository;

    private UserDataRepository userDataRepository;

    @Autowired
    public void setUserDataRepository(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

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
        if (DealType.BUY.getNominativeFirstLetterToUpper().equals(message)) {
            dealType = DealType.BUY;
        } else if (DealType.SELL.getNominativeFirstLetterToUpper().equals(message)) {
            dealType = DealType.SELL;
        } else {
            responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL);
            return;
        }
        FiatCurrency fiatCurrency = FiatCurrencyUtil.isFew()
                ? userDataRepository.getFiatCurrencyByChatId(chatId)
                : FiatCurrencyUtil.getFirst();
        List<PaymentType> paymentTypes = paymentTypeRepository.getByDealTypeAndFiatCurrency(dealType, fiatCurrency);
        if (CollectionUtils.isEmpty(paymentTypes)) {
            responseSender.sendMessage(chatId, "Список тип оплат на " + dealType.getAccusative() + " пуст."); //todo add fiat
            processToAdminMainPanel(chatId);
            return;
        }

        List<InlineButton> buttons = paymentTypes.stream()
                .map(paymentType -> InlineButton.builder()
                        .text(paymentType.getName())
                        .data(Command.CHANGE_MIN_SUM.getText()
                                + BotStringConstants.CALLBACK_DATA_SPLITTER + paymentType.getPid())
                        .build())
                .collect(Collectors.toList());
        responseSender.sendMessage(chatId, "Выберите тип оплаты для изменения минимальной суммы.",
                KeyboardUtil.buildInline(buttons));
        responseSender.sendMessage(chatId, "Для возвращения в меню нажмите \"Отмена\".", BotKeyboard.REPLY_CANCEL);
        userService.nextStep(chatId);
    }
}
