package tgb.btc.rce.service.processors.paymenttypes.turning;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
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
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.util.KeyboardUtil;
import tgb.btc.rce.util.UpdateUtil;
import tgb.btc.rce.vo.InlineButton;

import java.util.ArrayList;
import java.util.List;

@CommandProcessor(command = Command.TURN_PAYMENT_TYPES, step = 2)
public class ShowPaymentTypesForTurn extends Processor {

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
        sendPaymentTypes(chatId, dealType, fiatCurrency);
        processToAdminMainPanel(chatId);
    }

    public void sendPaymentTypes(Long chatId, DealType dealType, FiatCurrency fiatCurrency) {
        List<PaymentType> paymentTypes = paymentTypeRepository.getByDealTypeAndFiatCurrency(dealType, fiatCurrency);
        if (CollectionUtils.isEmpty(paymentTypes)) {
            responseSender.sendMessage(chatId, "Список тип оплат на " + dealType.getAccusative() + " пуст."); //todo fiat
            processToAdminMainPanel(chatId);
            return;
        }
        List<InlineButton> buttons = new ArrayList<>();
        for (PaymentType paymentType : paymentTypes) {
            boolean isOn = BooleanUtils.isTrue(paymentType.getOn());
            String text = paymentType.getName() + " - " +
                    (isOn ? "выключить" : "включить");
            String data = Command.TURNING_PAYMENT_TYPES.name()
                    + BotStringConstants.CALLBACK_DATA_SPLITTER + paymentType.getPid()
                    + BotStringConstants.CALLBACK_DATA_SPLITTER + (isOn ? Boolean.FALSE.toString() : Boolean.TRUE.toString());
            buttons.add(InlineButton.builder()
                    .text(text)
                    .data(data)
                    .build());
        }
        buttons.add(InlineButton.builder()
                .text("❌ Закрыть")
                .data(Command.INLINE_DELETE.name())
                .build());
        responseSender.sendMessage(chatId, "Выберите тип оплаты для включения/выключения.",
                KeyboardUtil.buildInline(buttons));
    }

}
