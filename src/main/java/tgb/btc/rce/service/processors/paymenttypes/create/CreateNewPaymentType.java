package tgb.btc.rce.service.processors.paymenttypes.create;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Update;
import tgb.btc.rce.annotation.CommandProcessor;
import tgb.btc.rce.bean.PaymentType;
import tgb.btc.rce.constants.BotStringConstants;
import tgb.btc.rce.enums.Command;
import tgb.btc.rce.enums.DealType;
import tgb.btc.rce.enums.FiatCurrency;
import tgb.btc.rce.repository.PaymentTypeRepository;
import tgb.btc.rce.repository.UserDataRepository;
import tgb.btc.rce.service.IResponseSender;
import tgb.btc.rce.service.Processor;
import tgb.btc.rce.service.impl.UserService;
import tgb.btc.rce.util.FiatCurrenciesUtil;
import tgb.btc.rce.util.UpdateUtil;

import java.math.BigDecimal;

@CommandProcessor(command = Command.NEW_PAYMENT_TYPE, step = 3)
public class CreateNewPaymentType extends Processor {

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

    @Autowired
    public CreateNewPaymentType(IResponseSender responseSender, UserService userService) {
        super(responseSender, userService);
    }

    @Override
    public void run(Update update) {
        Long chatId = UpdateUtil.getChatId(update);
        String message = UpdateUtil.getMessageText(update);
        DealType dealType;
        FiatCurrency fiatCurrency;
        if (FiatCurrenciesUtil.isFew()) {
            if (BotStringConstants.BUY.equals(message)) dealType = DealType.BUY;
            else if (BotStringConstants.SELL.equals(message)) dealType = DealType.SELL;
            else {
                responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL);
                return;
            }
            fiatCurrency = userDataRepository.getFiatCurrencyByChatId(chatId);
        } else {
            if (BotStringConstants.BUY.equals(message)) dealType = DealType.BUY;
            else if (BotStringConstants.SELL.equals(message)) dealType = DealType.SELL;
            else {
                responseSender.sendMessage(chatId, BotStringConstants.BUY_OR_SELL);
                return;
            }
            fiatCurrency = FiatCurrenciesUtil.getFirst();
        }
        PaymentType paymentType = new PaymentType();
        paymentType.setName(userDataRepository.getStringByUserChatId(chatId));
        paymentType.setDealType(dealType);
        paymentType.setMinSum(BigDecimal.ZERO);
        paymentType.setFiatCurrency(fiatCurrency);
        paymentTypeRepository.save(paymentType);
        responseSender.sendMessage(chatId, "Новый тип оплаты сохранен. " +
                "Не забудьте установить минимальную сумму, добавить реквизиты и включить по необходимости.");
        userService.setDefaultValues(chatId);
        processToAdminMainPanel(chatId);
    }

}
